variable "environment" {
  description = "Environment name"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "ami_id" {
  description = "ID of the AMI to use for EC2 instances"
  type        = string
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.micro"
}

variable "security_group_ids" {
  description = "List of security group IDs"
  type        = list(string)
}

variable "subnet_ids" {
  description = "List of subnet IDs for the Auto Scaling Group"
  type        = list(string)
}

variable "root_volume_size" {
  description = "Size of the root volume in GB"
  type        = number
  default     = 30
}

variable "desired_capacity" {
  description = "Desired number of instances in the Auto Scaling Group"
  type        = number
  default     = 2
}

variable "min_size" {
  description = "Minimum number of instances in the Auto Scaling Group"
  type        = number
  default     = 1
}

variable "max_size" {
  description = "Maximum number of instances in the Auto Scaling Group"
  type        = number
  default     = 4
}

variable "target_group_arns" {
  description = "List of target group ARNs for the Auto Scaling Group"
  type        = list(string)
}

variable "cpu_threshold_percent" {
  description = "CPU threshold percentage for scaling"
  type        = number
  default     = 70
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
  default     = {}
}

variable "enable_monitoring" {
  description = "Enable detailed monitoring for instances"
  type        = bool
  default     = true
}

variable "instance_warmup" {
  description = "Time in seconds until a newly launched instance is ready to use"
  type        = number
  default     = 300
}

variable "health_check_grace_period" {
  description = "Time in seconds after instance comes into service before checking health"
  type        = number
  default     = 300
}

variable "termination_policies" {
  description = "List of policies to use for selecting instances to terminate"
  type        = list(string)
  default     = ["Default"]
}

variable "scale_up_cooldown" {
  description = "Cooldown period in seconds before allowing another scale up activity"
  type        = number
  default     = 300
}

variable "scale_down_cooldown" {
  description = "Cooldown period in seconds before allowing another scale down activity"
  type        = number
  default     = 300
}

variable "additional_iam_policies" {
  description = "List of additional IAM policy ARNs to attach to the instance role"
  type        = list(string)
  default     = []
}

variable "user_data_vars" {
  description = "Additional variables to pass to user data template"
  type        = map(string)
  default     = {}
}