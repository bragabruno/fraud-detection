output "rds_security_group_id" {
  description = "ID of the RDS security group"
  value       = aws_security_group.rds.id
}

output "redis_security_group_id" {
  description = "ID of the Redis security group"
  value       = aws_security_group.redis.id
}

output "ec2_security_group_id" {
  description = "ID of the EC2 security group"
  value       = aws_security_group.ec2.id
}

output "monitoring_security_group_id" {
  description = "ID of the monitoring security group"
  value       = aws_security_group.monitoring.id
}

output "security_group_ids" {
  description = "Map of all security group IDs"
  value = {
    rds        = aws_security_group.rds.id
    redis      = aws_security_group.redis.id
    ec2        = aws_security_group.ec2.id
    monitoring = aws_security_group.monitoring.id
  }
}