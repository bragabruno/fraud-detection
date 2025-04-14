# General Configuration
environment = "production"
aws_region  = "us-east-1"

# Tags
tags = {
  ManagedBy    = "Terraform"
  Project      = "FraudDetection"
  Environment  = "production"
}

# VPC Configuration
vpc_cidr = "10.0.0.0/16"
availability_zones = ["us-east-1a", "us-east-1b", "us-east-1c"]
private_subnets    = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
public_subnets     = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]
allowed_cidr_blocks = ["0.0.0.0/0"]  # Should be restricted in production

# RDS Configuration
rds_db_name        = "frauddetection"
rds_username       = "admin"  # Should be replaced with secure value from vault/parameter store
rds_password       = "REPLACE_WITH_SECURE_PASSWORD"  # Should be replaced with secure value from vault/parameter store
rds_instance_class = "db.r5.large"

# Redis Configuration
redis_node_type         = "cache.t3.medium"
redis_num_cache_clusters = 2
redis_auth_token        = "REPLACE_WITH_SECURE_TOKEN"  # Should be replaced with secure value from vault/parameter store

# EC2 Configuration
ec2_ami_id           = "ami-0c55b159cbfafe1f0"  # Replace with latest Amazon Linux 2 AMI
ec2_instance_type    = "t3.medium"
ec2_root_volume_size = 30

# Auto Scaling Configuration
asg_min_size         = 1
asg_max_size         = 4
asg_desired_capacity = 2

# Load Balancer Configuration
target_group_arns    = []  # Should be populated with actual ALB target group ARNs
load_balancer_name   = "fraud-detection-alb"

# Monitoring Configuration
alarm_actions = []  # Should be populated with SNS topic ARNs

# Backup and Maintenance Windows
backup_window      = "03:00-04:00"
maintenance_window = "Mon:04:00-Mon:05:00"

# Threshold Configuration
cpu_threshold_percent    = 70
memory_threshold_percent = 80
disk_threshold_percent   = 85

# VPC Flow Logs
vpc_flow_log_retention_days = 30