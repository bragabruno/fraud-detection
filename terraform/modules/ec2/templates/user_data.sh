#!/bin/bash
set -e

# Configure CloudWatch agent
yum install -y amazon-cloudwatch-agent

# Create CloudWatch agent configuration
cat > /opt/aws/amazon-cloudwatch-agent/bin/config.json << 'EOF'
{
    "agent": {
        "metrics_collection_interval": 60,
        "run_as_user": "root"
    },
    "metrics": {
        "metrics_collected": {
            "cpu": {
                "measurement": [
                    "cpu_usage_idle",
                    "cpu_usage_iowait",
                    "cpu_usage_user",
                    "cpu_usage_system"
                ],
                "totalcpu": true
            },
            "disk": {
                "measurement": [
                    "used_percent",
                    "inodes_free"
                ],
                "resources": [
                    "*"
                ]
            },
            "diskio": {
                "measurement": [
                    "io_time",
                    "write_bytes",
                    "read_bytes",
                    "writes",
                    "reads"
                ],
                "resources": [
                    "*"
                ]
            },
            "mem": {
                "measurement": [
                    "mem_used_percent",
                    "mem_total",
                    "mem_used",
                    "mem_free"
                ]
            },
            "swap": {
                "measurement": [
                    "swap_used_percent",
                    "swap_used",
                    "swap_free"
                ]
            }
        }
    },
    "logs": {
        "logs_collected": {
            "files": {
                "collect_list": [
                    {
                        "file_path": "/var/log/messages",
                        "log_group_name": "${environment}/var/log/messages",
                        "log_stream_name": "{instance_id}"
                    },
                    {
                        "file_path": "/var/log/secure",
                        "log_group_name": "${environment}/var/log/secure",
                        "log_stream_name": "{instance_id}"
                    }
                ]
            }
        }
    }
}
EOF

# Start CloudWatch agent
/opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/opt/aws/amazon-cloudwatch-agent/bin/config.json
systemctl start amazon-cloudwatch-agent

# Install AWS SSM agent
yum install -y https://s3.${region}.amazonaws.com/amazon-ssm-${region}/latest/linux_amd64/amazon-ssm-agent.rpm
systemctl start amazon-ssm-agent
systemctl enable amazon-ssm-agent

# Set timezone
timedatectl set-timezone UTC

# Update system packages
yum update -y

# Configure basic security
sed -i 's/#MaxAuthTries.*/MaxAuthTries 3/' /etc/ssh/sshd_config
sed -i 's/#LogLevel.*/LogLevel VERBOSE/' /etc/ssh/sshd_config
systemctl restart sshd

# Set up log rotation
cat > /etc/logrotate.d/custom << 'EOF'
/var/log/messages /var/log/secure {
    rotate 7
    daily
    missingok
    notifempty
    compress
    sharedscripts
    postrotate
        /bin/kill -HUP `cat /var/run/syslogd.pid 2> /dev/null` 2> /dev/null || true
    endscript
}
EOF

# Configure sysctl parameters
cat >> /etc/sysctl.conf << 'EOF'
# Increase system file descriptor limit
fs.file-max = 65535

# Increase TCP max buffer size
net.core.rmem_max = 16777216
net.core.wmem_max = 16777216

# Enable TCP window scaling
net.ipv4.tcp_window_scaling = 1

# Increase TCP auto tuning limits
net.ipv4.tcp_rmem = 4096 87380 16777216
net.ipv4.tcp_wmem = 4096 65536 16777216
EOF

sysctl -p

# Final message
echo "Instance initialization completed"