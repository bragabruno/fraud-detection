output "rds_instance_id" {
  description = "ID of the RDS instance"
  value       = aws_db_instance.main.id
}

output "rds_instance_address" {
  description = "Address of the RDS instance"
  value       = aws_db_instance.main.address
}

output "rds_instance_endpoint" {
  description = "Connection endpoint of the RDS instance"
  value       = aws_db_instance.main.endpoint
}

output "rds_instance_arn" {
  description = "ARN of the RDS instance"
  value       = aws_db_instance.main.arn
}

output "rds_subnet_group_id" {
  description = "ID of the RDS subnet group"
  value       = aws_db_subnet_group.main.id
}

output "rds_subnet_group_arn" {
  description = "ARN of the RDS subnet group"
  value       = aws_db_subnet_group.main.arn
}

output "rds_parameter_group_id" {
  description = "ID of the RDS parameter group"
  value       = aws_db_parameter_group.main.id
}

output "rds_parameter_group_arn" {
  description = "ARN of the RDS parameter group"
  value       = aws_db_parameter_group.main.arn
}

output "rds_enhanced_monitoring_role_arn" {
  description = "ARN of the enhanced monitoring IAM role"
  value       = aws_iam_role.rds_enhanced_monitoring.arn
}

output "db_name" {
  description = "Name of the database"
  value       = aws_db_instance.main.db_name
}

output "cloudwatch_alarms" {
  description = "Map of CloudWatch alarms created for the RDS instance"
  value = {
    cpu     = aws_cloudwatch_metric_alarm.rds_cpu.arn
    memory  = aws_cloudwatch_metric_alarm.rds_memory.arn
    storage = aws_cloudwatch_metric_alarm.rds_storage.arn
  }
}

output "monitoring_details" {
  description = "Monitoring configuration details"
  value = {
    monitoring_interval              = aws_db_instance.main.monitoring_interval
    performance_insights_enabled     = aws_db_instance.main.performance_insights_enabled
    performance_insights_retention   = aws_db_instance.main.performance_insights_retention_period
    enhanced_monitoring_role_name    = aws_iam_role.rds_enhanced_monitoring.name
  }
}