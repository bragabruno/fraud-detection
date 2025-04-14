variable "environment" {
  description = "Environment name"
  type        = string
}

variable "vpc_id" {
  description = "ID of the VPC"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs for the Redis cluster"
  type        = list(string)
}

variable "security_group_ids" {
  description = "List of security group IDs for the Redis cluster"
  type        = list(string)
}

variable "node_type" {
  description = "The compute and memory capacity of the nodes"
  type        = string
  default     = "cache.t3.medium"
}

variable "num_cache_clusters" {
  description = "The number of cache clusters (primary and replicas) this replication group will have"
  type        = number
  default     = 2
}

variable "maintenance_window" {
  description = "Specifies the weekly time range for when maintenance on the cache cluster is performed"
  type        = string
  default     = "sun:05:00-sun:06:00"
}

variable "snapshot_window" {
  description = "The daily time range (in UTC) during which ElastiCache will begin taking a daily snapshot of your cache cluster"
  type        = string
  default     = "04:00-05:00"
}

variable "snapshot_retention_limit" {
  description = "The number of days for which ElastiCache will retain automatic cache cluster snapshots"
  type        = number
  default     = 7
}

variable "auth_token" {
  description = "Password used to access a password protected server. Can be specified only if transit_encryption_enabled = true"
  type        = string
  sensitive   = true
}

variable "alarm_actions" {
  description = "List of ARNs to be notified via CloudWatch when alarm enters ALARM state"
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
  default     = {}
}

variable "parameter_group_family" {
  description = "The family of the ElastiCache parameter group"
  type        = string
  default     = "redis6.x"
}

variable "apply_immediately" {
  description = "Specifies whether any modifications are applied immediately, or during the next maintenance window"
  type        = bool
  default     = false
}

variable "at_rest_encryption_enabled" {
  description = "Whether to enable encryption at rest"
  type        = bool
  default     = true
}

variable "transit_encryption_enabled" {
  description = "Whether to enable encryption in transit"
  type        = bool
  default     = true
}