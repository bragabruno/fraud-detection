# VPC Outputs
output "vpc_id" {
  description = "ID of the created VPC"
  value       = module.vpc.vpc_id
}

output "vpc_cidr" {
  description = "CIDR block of the created VPC"
  value       = module.vpc.vpc_cidr
}

output "private_subnet_ids" {
  description = "List of private subnet IDs"
  value       = module.vpc.private_subnet_ids
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = module.vpc.public_subnet_ids
}

# RDS Outputs
output "rds_endpoint" {
  description = "Connection endpoint for the RDS instance"
  value       = module.rds.rds_instance_endpoint
}

output "rds_instance_id" {
  description = "ID of the RDS instance"
  value       = module.rds.rds_instance_id
}

output "rds_monitoring_details" {
  description = "Monitoring configuration for RDS"
  value       = module.rds.monitoring_details
}

# Redis Outputs
output "redis_endpoint" {
  description = "Primary endpoint for the Redis cluster"
  value       = module.redis.primary_endpoint
}

output "redis_reader_endpoint" {
  description = "Reader endpoint for the Redis cluster"
  value       = module.redis.reader_endpoint
}

# EC2 Outputs
output "asg_name" {
  description = "Name of the Auto Scaling Group"
  value       = module.ec2.autoscaling_group_name
}

output "launch_template_id" {
  description = "ID of the EC2 launch template"
  value       = module.ec2.launch_template_id
}

output "ec2_role_arn" {
  description = "ARN of the IAM role attached to EC2 instances"
  value       = module.ec2.instance_role_arn
}

# Security Group Outputs
output "security_groups" {
  description = "Map of all security group IDs"
  value       = module.security.security_group_ids
}

# Monitoring Outputs
output "cloudwatch_dashboard" {
  description = "ARN of the CloudWatch dashboard"
  value       = module.monitoring.dashboard_arn
}

output "monitoring_alarms" {
  description = "Map of all CloudWatch alarms"
  value = {
    rds_alarms   = module.rds.cloudwatch_alarms
    redis_alarms = module.redis.cloudwatch_alarms
    ec2_alarms   = module.ec2.cloudwatch_alarms
  }
}

# Infrastructure Overview
output "infrastructure_details" {
  description = "Overview of the entire infrastructure"
  value = {
    environment = var.environment
    region      = var.aws_region
    vpc = {
      id            = module.vpc.vpc_id
      cidr          = module.vpc.vpc_cidr
      num_subnets   = length(module.vpc.private_subnet_ids) + length(module.vpc.public_subnet_ids)
    }
    databases = {
      rds = {
        identifier = module.rds.rds_instance_id
        endpoint   = module.rds.rds_instance_endpoint
      }
      redis = {
        cluster_id = module.redis.cluster_id
        endpoint   = module.redis.primary_endpoint
      }
    }
    compute = {
      asg_name           = module.ec2.autoscaling_group_name
      min_size           = module.ec2.asg_configuration.min_size
      max_size           = module.ec2.asg_configuration.max_size
      desired_capacity   = module.ec2.asg_configuration.desired_capacity
      instance_type      = module.ec2.asg_configuration.instance_type
    }
    monitoring = {
      dashboard_arn = module.monitoring.dashboard_arn
      num_alarms    = length(module.monitoring.alarms_map)
    }
  }
}