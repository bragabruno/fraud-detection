# General Variables
variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
  default     = {
    ManagedBy = "Terraform"
    Project   = "FraudDetection"
  }
}

# VPC Variables
variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

variable "private_subnets" {
  description = "Private subnet CIDR blocks"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "public_subnets" {
  description = "Public subnet CIDR blocks"
  type        = list(string)
  default     = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]
}

variable "allowed_cidr_blocks" {
  description = "CIDR blocks allowed to access the resources"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

# RDS Variables
variable "rds_db_name" {
  description = "Name of the RDS database"
  type        = string
  default     = "frauddetection"
}

variable "rds_username" {
  description = "Master username for RDS"
  type        = string
  sensitive   = true
}

variable "rds_password" {
  description = "Master password for RDS"
  type        = string
  sensitive   = true
}

variable "rds_instance_class" {
  description = "Instance class for RDS"
  type        = string
  default     = "db.r5.large"
}

# Redis Variables
variable "redis_node_type" {
  description = "Node type for Redis cluster"
  type        = string
  default     = "cache.t3.medium"
}

variable "redis_num_cache_clusters" {
  description = "Number of cache clusters for Redis"
  type        = number
  default     = 2
}

variable "redis_auth_token" {
  description = "Auth token for Redis cluster"
  type        = string
  sensitive   = true
}

# EC2 Variables
variable "ec2_ami_id" {
  description = "AMI ID for EC2 instances"
  type        = string
}

variable "ec2_instance_type" {
  description = "Instance type for EC2"
  type        = string
  default     = "t3.medium"
}

variable "ec2_root_volume_size" {
  description = "Root volume size for EC2 instances"
  type        = number
  default     = 30
}

# Auto Scaling Group Variables
variable "asg_min_size" {
  description = "Minimum size of Auto Scaling Group"
  type        = number
  default     = 1
}

variable "asg_max_size" {
  description = "Maximum size of Auto Scaling Group"
  type        = number
  default     = 4
}

variable "asg_desired_capacity" {
  description = "Desired capacity of Auto Scaling Group"
  type        = number
  default     = 2
}

variable "target_group_arns" {
  description = "List of target group ARNs for Auto Scaling Group"
  type        = list(string)
}

# Monitoring Variables
variable "alarm_actions" {
  description = "List of ARNs to notify when alarms trigger"
  type        = list(string)
}

variable "load_balancer_name" {
  description = "Name of the load balancer for monitoring"
  type        = string
}

# VPC Flow Logs
variable "vpc_flow_log_retention_days" {
  description = "Number of days to retain VPC Flow Logs"
  type        = number
  default     = 30
}

# Metrics and Monitoring Thresholds
variable "cpu_threshold_percent" {
  description = "CPU threshold percentage for scaling"
  type        = number
  default     = 70
}

variable "memory_threshold_percent" {
  description = "Memory threshold percentage for alerts"
  type        = number
  default     = 80
}

variable "disk_threshold_percent" {
  description = "Disk usage threshold percentage for alerts"
  type        = number
  default     = 85
}

# Backup and Maintenance Windows
variable "backup_window" {
  description = "Preferred backup window"
  type        = string
  default     = "03:00-04:00"
}

variable "maintenance_window" {
  description = "Preferred maintenance window"
  type        = string
  default     = "Mon:04:00-Mon:05:00"
}