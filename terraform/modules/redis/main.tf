# Subnet group for Redis cluster
resource "aws_elasticache_subnet_group" "main" {
  name        = "${var.environment}-redis-subnet-group"
  description = "Redis subnet group for ${var.environment}"
  subnet_ids  = var.subnet_ids

  tags = merge(
    {
      Name        = "${var.environment}-redis-subnet-group"
      Environment = var.environment
    },
    var.tags
  )
}

# Parameter group for Redis
resource "aws_elasticache_parameter_group" "main" {
  family = "redis6.x"
  name   = "${var.environment}-redis-params"

  parameter {
    name  = "maxmemory-policy"
    value = "volatile-lru"
  }

  parameter {
    name  = "notify-keyspace-events"
    value = "Ex"
  }

  tags = merge(
    {
      Name        = "${var.environment}-redis-params"
      Environment = var.environment
    },
    var.tags
  )
}

# Redis replication group
resource "aws_elasticache_replication_group" "main" {
  replication_group_id = "${var.environment}-redis-cluster"
  description         = "Redis cluster for ${var.environment}"
  
  node_type            = var.node_type
  port                = 6379
  parameter_group_name = aws_elasticache_parameter_group.main.name
  
  subnet_group_name    = aws_elasticache_subnet_group.main.name
  security_group_ids   = var.security_group_ids

  # Cluster settings
  num_cache_clusters         = var.num_cache_clusters
  automatic_failover_enabled = true
  multi_az_enabled          = true

  # Redis settings
  engine                     = "redis"
  engine_version             = "6.x"
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true
  
  # Maintenance settings
  maintenance_window         = var.maintenance_window
  snapshot_window           = var.snapshot_window
  snapshot_retention_limit  = var.snapshot_retention_limit

  # Auth settings
  auth_token                = var.auth_token

  tags = merge(
    {
      Name        = "${var.environment}-redis-cluster"
      Environment = var.environment
    },
    var.tags
  )
}

# CloudWatch alarms for Redis monitoring
resource "aws_cloudwatch_metric_alarm" "redis_cpu" {
  alarm_name          = "${var.environment}-redis-high-cpu"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ElastiCache"
  period             = "300"
  statistic          = "Average"
  threshold          = "75"
  alarm_description  = "Redis cluster CPU utilization"
  alarm_actions      = var.alarm_actions

  dimensions = {
    CacheClusterId = aws_elasticache_replication_group.main.id
  }

  tags = merge(
    {
      Name        = "${var.environment}-redis-high-cpu"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_cloudwatch_metric_alarm" "redis_memory" {
  alarm_name          = "${var.environment}-redis-low-memory"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "FreeableMemory"
  namespace           = "AWS/ElastiCache"
  period             = "300"
  statistic          = "Average"
  threshold          = "100000000" # 100MB in bytes
  alarm_description  = "Redis cluster freeable memory"
  alarm_actions      = var.alarm_actions

  dimensions = {
    CacheClusterId = aws_elasticache_replication_group.main.id
  }

  tags = merge(
    {
      Name        = "${var.environment}-redis-low-memory"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_cloudwatch_metric_alarm" "redis_connections" {
  alarm_name          = "${var.environment}-redis-connections"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "CurrConnections"
  namespace           = "AWS/ElastiCache"
  period             = "300"
  statistic          = "Average"
  threshold          = "5000"
  alarm_description  = "Redis cluster current connections"
  alarm_actions      = var.alarm_actions

  dimensions = {
    CacheClusterId = aws_elasticache_replication_group.main.id
  }

  tags = merge(
    {
      Name        = "${var.environment}-redis-connections"
      Environment = var.environment
    },
    var.tags
  )
}