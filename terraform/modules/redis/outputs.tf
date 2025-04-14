output "cluster_id" {
  description = "Redis cluster ID"
  value       = aws_elasticache_replication_group.main.id
}

output "primary_endpoint" {
  description = "Address of the endpoint for the primary node in the replication group"
  value       = aws_elasticache_replication_group.main.primary_endpoint_address
}

output "reader_endpoint" {
  description = "Address of the endpoint for the reader node in the replication group"
  value       = aws_elasticache_replication_group.main.reader_endpoint_address
}

output "port" {
  description = "Port number on which the Redis cluster accepts connections"
  value       = aws_elasticache_replication_group.main.port
}

output "subnet_group_name" {
  description = "Name of the created ElastiCache subnet group"
  value       = aws_elasticache_subnet_group.main.name
}

output "parameter_group_name" {
  description = "Name of the created ElastiCache parameter group"
  value       = aws_elasticache_parameter_group.main.name
}

output "security_group_ids" {
  description = "Security group IDs for the Redis cluster"
  value       = var.security_group_ids
}

output "maintenance_window" {
  description = "Maintenance window for the Redis cluster"
  value       = aws_elasticache_replication_group.main.maintenance_window
}

output "snapshot_window" {
  description = "Snapshot window for the Redis cluster"
  value       = aws_elasticache_replication_group.main.snapshot_window
}

output "num_cache_clusters" {
  description = "Number of cache clusters in the replication group"
  value       = aws_elasticache_replication_group.main.num_cache_clusters
}

output "cloudwatch_alarms" {
  description = "Map of CloudWatch alarms created for the Redis cluster"
  value = {
    cpu         = aws_cloudwatch_metric_alarm.redis_cpu.arn
    memory      = aws_cloudwatch_metric_alarm.redis_memory.arn
    connections = aws_cloudwatch_metric_alarm.redis_connections.arn
  }
}

output "auth_token_enabled" {
  description = "Whether authentication token (password) is enabled"
  value       = var.auth_token != null
  sensitive   = true
}

output "at_rest_encryption_enabled" {
  description = "Whether at-rest encryption is enabled"
  value       = aws_elasticache_replication_group.main.at_rest_encryption_enabled
}

output "transit_encryption_enabled" {
  description = "Whether in-transit encryption is enabled"
  value       = aws_elasticache_replication_group.main.transit_encryption_enabled
}