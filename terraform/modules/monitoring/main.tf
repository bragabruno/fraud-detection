# CloudWatch Dashboard for Infrastructure Monitoring
resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "${var.environment}-infrastructure-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      # RDS Metrics
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6

        properties = {
          metrics = [
            ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", var.rds_instance_id],
            [".", "FreeableMemory", ".", "."],
            [".", "FreeStorageSpace", ".", "."],
            [".", "DatabaseConnections", ".", "."]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "RDS Metrics"
        }
      },
      # Redis Metrics
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6

        properties = {
          metrics = [
            ["AWS/ElastiCache", "CPUUtilization", "CacheClusterId", var.redis_cluster_id],
            [".", "FreeableMemory", ".", "."],
            [".", "CurrConnections", ".", "."],
            [".", "CacheHits", ".", "."],
            [".", "CacheMisses", ".", "."]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "Redis Metrics"
        }
      },
      # EC2 Metrics
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6

        properties = {
          metrics = [
            ["AWS/EC2", "CPUUtilization", "InstanceId", var.ec2_instance_ids[0]],
            [".", "MemoryUtilization", ".", "."],
            [".", "DiskReadOps", ".", "."],
            [".", "DiskWriteOps", ".", "."]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "EC2 Metrics"
        }
      }
    ]
  })
}

# High-Level Service Health Alarms
resource "aws_cloudwatch_metric_alarm" "service_health" {
  alarm_name          = "${var.environment}-service-health"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "HealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  period             = "300"
  statistic          = "Average"
  threshold          = "1"
  alarm_description  = "This metric monitors service health across the infrastructure"
  alarm_actions      = var.alarm_actions

  dimensions = {
    LoadBalancer = var.load_balancer_name
  }

  tags = merge(
    {
      Name        = "${var.environment}-service-health"
      Environment = var.environment
    },
    var.tags
  )
}

# VPC Flow Logs Metric Filter
resource "aws_cloudwatch_log_metric_filter" "vpc_rejected_connections" {
  name           = "${var.environment}-vpc-rejected-connections"
  pattern        = "[version, account, eni, source, destination, srcport, destport, protocol, packets, bytes, windowstart, windowend, action=REJECT*, ...]"
  log_group_name = var.vpc_flow_log_group

  metric_transformation {
    name          = "RejectedConnections"
    namespace     = "VPC/Security"
    value         = "1"
    default_value = "0"
  }
}

# VPC Flow Logs Alarm
resource "aws_cloudwatch_metric_alarm" "vpc_rejected_connections" {
  alarm_name          = "${var.environment}-vpc-rejected-connections"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "RejectedConnections"
  namespace           = "VPC/Security"
  period             = "300"
  statistic          = "Sum"
  threshold          = var.vpc_rejected_connections_threshold
  alarm_description  = "This metric monitors rejected VPC connections"
  alarm_actions      = var.alarm_actions

  tags = merge(
    {
      Name        = "${var.environment}-vpc-rejected-connections"
      Environment = var.environment
    },
    var.tags
  )
}

# Application Latency Alarm
resource "aws_cloudwatch_metric_alarm" "application_latency" {
  alarm_name          = "${var.environment}-high-application-latency"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "TargetResponseTime"
  namespace           = "AWS/ApplicationELB"
  period             = "300"
  statistic          = "Average"
  threshold          = var.application_latency_threshold
  alarm_description  = "This metric monitors application latency"
  alarm_actions      = var.alarm_actions

  dimensions = {
    LoadBalancer = var.load_balancer_name
  }

  tags = merge(
    {
      Name        = "${var.environment}-high-application-latency"
      Environment = var.environment
    },
    var.tags
  )
}

# Error Rate Alarm
resource "aws_cloudwatch_metric_alarm" "error_rate" {
  alarm_name          = "${var.environment}-high-error-rate"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "HTTPCode_Target_5XX_Count"
  namespace           = "AWS/ApplicationELB"
  period             = "300"
  statistic          = "Sum"
  threshold          = var.error_rate_threshold
  alarm_description  = "This metric monitors application error rate"
  alarm_actions      = var.alarm_actions

  dimensions = {
    LoadBalancer = var.load_balancer_name
  }

  tags = merge(
    {
      Name        = "${var.environment}-high-error-rate"
      Environment = var.environment
    },
    var.tags
  )
}