resource "aws_db_subnet_group" "main" {
  name        = "${var.environment}-rds-subnet-group"
  description = "RDS subnet group for ${var.environment}"
  subnet_ids  = var.subnet_ids

  tags = merge(
    {
      Name        = "${var.environment}-rds-subnet-group"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_db_parameter_group" "main" {
  name   = "${var.environment}-postgres-params"
  family = "postgres14"

  parameter {
    name  = "log_min_duration_statement"
    value = "1000"
  }

  parameter {
    name  = "log_connections"
    value = "1"
  }

  parameter {
    name  = "log_disconnections"
    value = "1"
  }

  parameter {
    name  = "log_lock_waits"
    value = "1"
  }

  parameter {
    name  = "log_temp_files"
    value = "0"
  }

  tags = merge(
    {
      Name        = "${var.environment}-postgres-params"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_db_instance" "main" {
  identifier = "${var.environment}-${var.db_name}"
  
  # Engine configuration
  engine               = "postgres"
  engine_version       = "14"
  instance_class       = var.instance_class
  allocated_storage    = var.allocated_storage
  storage_type         = "gp3"
  storage_encrypted    = true
  
  # Database configuration
  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 5432

  # Network configuration
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = var.security_group_ids
  publicly_accessible    = false
  multi_az              = var.multi_az

  # Backup configuration
  backup_retention_period = var.backup_retention_period
  backup_window          = "03:00-04:00"
  maintenance_window     = "Mon:04:00-Mon:05:00"

  # Enhanced monitoring
  monitoring_interval = 60
  monitoring_role_arn = aws_iam_role.rds_enhanced_monitoring.arn
  
  # Performance insights
  performance_insights_enabled          = true
  performance_insights_retention_period = 7

  # Parameter group
  parameter_group_name = aws_db_parameter_group.main.name

  # Additional configuration
  auto_minor_version_upgrade = true
  copy_tags_to_snapshot     = true
  deletion_protection       = true
  skip_final_snapshot      = false
  final_snapshot_identifier = "${var.environment}-${var.db_name}-final-snapshot"

  tags = merge(
    {
      Name        = "${var.environment}-${var.db_name}"
      Environment = var.environment
    },
    var.tags
  )
}

# Enhanced Monitoring IAM Role
resource "aws_iam_role" "rds_enhanced_monitoring" {
  name = "${var.environment}-rds-enhanced-monitoring"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "monitoring.rds.amazonaws.com"
        }
      }
    ]
  })

  tags = merge(
    {
      Name        = "${var.environment}-rds-enhanced-monitoring"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_iam_role_policy_attachment" "rds_enhanced_monitoring" {
  role       = aws_iam_role.rds_enhanced_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

# CloudWatch Alarms for RDS Monitoring
resource "aws_cloudwatch_metric_alarm" "rds_cpu" {
  alarm_name          = "${var.environment}-${var.db_name}-high-cpu"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period             = "300"
  statistic          = "Average"
  threshold          = "80"
  alarm_description  = "This metric monitors RDS CPU utilization"
  alarm_actions      = var.alarm_actions

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.id
  }

  tags = merge(
    {
      Name        = "${var.environment}-${var.db_name}-high-cpu"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_cloudwatch_metric_alarm" "rds_memory" {
  alarm_name          = "${var.environment}-${var.db_name}-low-memory"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "FreeableMemory"
  namespace           = "AWS/RDS"
  period             = "300"
  statistic          = "Average"
  threshold          = "1000000000" # 1GB in bytes
  alarm_description  = "This metric monitors RDS freeable memory"
  alarm_actions      = var.alarm_actions

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.id
  }

  tags = merge(
    {
      Name        = "${var.environment}-${var.db_name}-low-memory"
      Environment = var.environment
    },
    var.tags
  )
}

resource "aws_cloudwatch_metric_alarm" "rds_storage" {
  alarm_name          = "${var.environment}-${var.db_name}-low-storage"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period             = "300"
  statistic          = "Average"
  threshold          = "20000000000" # 20GB in bytes
  alarm_description  = "This metric monitors RDS free storage space"
  alarm_actions      = var.alarm_actions

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.id
  }

  tags = merge(
    {
      Name        = "${var.environment}-${var.db_name}-low-storage"
      Environment = var.environment
    },
    var.tags
  )
}