variable "environment" {
  description = "Environment name"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "rds_instance_id" {
  description = "ID of the RDS instance to monitor"
  type        = string
}

variable "redis_cluster_id" {
  description = "ID of the Redis cluster to monitor"
  type        = string
}

variable "ec2_instance_ids" {
  description = "List of EC2 instance IDs to monitor"
  type        = list(string)
}

variable "vpc_flow_log_group" {
  description = "Name of the CloudWatch Log Group for VPC Flow Logs"
  type        = string
}

variable "load_balancer_name" {
  description = "Name of the Application Load Balancer"
  type        = string
}

variable "alarm_actions" {
  description = "List of ARNs to notify when an alarm triggers"
  type        = list(string)
}

variable "vpc_rejected_connections_threshold" {
  description = "Threshold for VPC rejected connections alarm"
  type        = number
  default     = 100
}

variable "application_latency_threshold" {
  description = "Threshold in seconds for application latency alarm"
  type        = number
  default     = 1
}

variable "error_rate_threshold" {
  description = "Threshold for 5XX error rate alarm"
  type        = number
  default     = 10
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
  default     = {}
}

variable "monitoring_interval" {
  description = "The interval in seconds for enhanced monitoring"
  type        = number
  default     = 60
}

variable "dashboard_refresh_interval" {
  description = "Dashboard refresh interval in seconds"
  type        = number
  default     = 300
}

variable "retention_in_days" {
  description = "Number of days to retain CloudWatch log events"
  type        = number
  default     = 30
}

variable "alarm_evaluation_periods" {
  description = "Number of periods to evaluate for alarm conditions"
  type        = number
  default     = 2
}

variable "alarm_period" {
  description = "Period in seconds over which to evaluate alarms"
  type        = number
  default     = 300
}