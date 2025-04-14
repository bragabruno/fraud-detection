output "dashboard_arn" {
  description = "ARN of the CloudWatch dashboard"
  value       = aws_cloudwatch_dashboard.main.dashboard_arn
}

output "service_health_alarm" {
  description = "Details of the service health alarm"
  value = {
    arn         = aws_cloudwatch_metric_alarm.service_health.arn
    name        = aws_cloudwatch_metric_alarm.service_health.alarm_name
    description = aws_cloudwatch_metric_alarm.service_health.alarm_description
  }
}

output "vpc_flow_logs_metric_filter" {
  description = "Details of the VPC flow logs metric filter"
  value = {
    name          = aws_cloudwatch_log_metric_filter.vpc_rejected_connections.name
    pattern       = aws_cloudwatch_log_metric_filter.vpc_rejected_connections.pattern
    metric_name   = aws_cloudwatch_log_metric_filter.vpc_rejected_connections.metric_transformation[0].name
    metric_namespace = aws_cloudwatch_log_metric_filter.vpc_rejected_connections.metric_transformation[0].namespace
  }
}

output "vpc_rejected_connections_alarm" {
  description = "Details of the VPC rejected connections alarm"
  value = {
    arn         = aws_cloudwatch_metric_alarm.vpc_rejected_connections.arn
    name        = aws_cloudwatch_metric_alarm.vpc_rejected_connections.alarm_name
    description = aws_cloudwatch_metric_alarm.vpc_rejected_connections.alarm_description
  }
}

output "application_latency_alarm" {
  description = "Details of the application latency alarm"
  value = {
    arn         = aws_cloudwatch_metric_alarm.application_latency.arn
    name        = aws_cloudwatch_metric_alarm.application_latency.alarm_name
    description = aws_cloudwatch_metric_alarm.application_latency.alarm_description
    threshold   = aws_cloudwatch_metric_alarm.application_latency.threshold
  }
}

output "error_rate_alarm" {
  description = "Details of the error rate alarm"
  value = {
    arn         = aws_cloudwatch_metric_alarm.error_rate.arn
    name        = aws_cloudwatch_metric_alarm.error_rate.alarm_name
    description = aws_cloudwatch_metric_alarm.error_rate.alarm_description
    threshold   = aws_cloudwatch_metric_alarm.error_rate.threshold
  }
}

output "alarms_map" {
  description = "Map of all CloudWatch alarms created"
  value = {
    service_health         = aws_cloudwatch_metric_alarm.service_health.arn
    vpc_rejected          = aws_cloudwatch_metric_alarm.vpc_rejected_connections.arn
    application_latency   = aws_cloudwatch_metric_alarm.application_latency.arn
    error_rate           = aws_cloudwatch_metric_alarm.error_rate.arn
  }
}

output "monitoring_configuration" {
  description = "General monitoring configuration details"
  value = {
    environment                    = var.environment
    dashboard_refresh_interval     = var.dashboard_refresh_interval
    monitoring_interval           = var.monitoring_interval
    retention_in_days            = var.retention_in_days
    alarm_evaluation_periods     = var.alarm_evaluation_periods
    alarm_period                = var.alarm_period
  }
}

output "monitored_resources" {
  description = "List of resources being monitored"
  value = {
    rds_instance_id     = var.rds_instance_id
    redis_cluster_id    = var.redis_cluster_id
    ec2_instance_ids    = var.ec2_instance_ids
    load_balancer_name  = var.load_balancer_name
  }
}