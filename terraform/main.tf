provider "aws" {
  region = var.aws_region
}

# VPC Module
module "vpc" {
  source = "./modules/vpc"
  
  environment         = var.environment
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
  private_subnets    = var.private_subnets
  public_subnets     = var.public_subnets
  tags               = var.tags
}

# Security Module
module "security" {
  source = "./modules/security"
  
  environment        = var.environment
  vpc_id            = module.vpc.vpc_id
  allowed_cidr_blocks = var.allowed_cidr_blocks
  tags              = var.tags
}

# RDS Module
module "rds" {
  source = "./modules/rds"
  
  environment         = var.environment
  vpc_id             = module.vpc.vpc_id
  subnet_ids         = module.vpc.private_subnet_ids
  security_group_ids = [module.security.rds_security_group_id]
  
  db_name            = var.rds_db_name
  db_username        = var.rds_username
  db_password        = var.rds_password
  instance_class     = var.rds_instance_class
  multi_az          = true
  backup_retention_period = 35
  tags              = var.tags
  alarm_actions     = var.alarm_actions
}

# Redis Module
module "redis" {
  source = "./modules/redis"
  
  environment         = var.environment
  vpc_id             = module.vpc.vpc_id
  subnet_ids         = module.vpc.private_subnet_ids
  security_group_ids = [module.security.redis_security_group_id]
  node_type          = var.redis_node_type
  num_cache_clusters = var.redis_num_cache_clusters
  auth_token         = var.redis_auth_token
  tags               = var.tags
  alarm_actions      = var.alarm_actions
}

# EC2 Module
module "ec2" {
  source = "./modules/ec2"
  
  environment         = var.environment
  aws_region         = var.aws_region
  ami_id             = var.ec2_ami_id
  instance_type      = var.ec2_instance_type
  subnet_ids         = module.vpc.private_subnet_ids
  security_group_ids = [module.security.ec2_security_group_id]
  target_group_arns  = var.target_group_arns
  
  min_size           = var.asg_min_size
  max_size           = var.asg_max_size
  desired_capacity   = var.asg_desired_capacity
  
  root_volume_size   = var.ec2_root_volume_size
  tags               = var.tags
}

# Monitoring Module
module "monitoring" {
  source = "./modules/monitoring"
  
  environment         = var.environment
  aws_region         = var.aws_region
  vpc_flow_log_group = module.vpc.vpc_flow_log_group
  rds_instance_id    = module.rds.rds_instance_id
  redis_cluster_id   = module.redis.cluster_id
  ec2_instance_ids   = [module.ec2.autoscaling_group_name]
  load_balancer_name = var.load_balancer_name
  alarm_actions      = var.alarm_actions
  tags              = var.tags
}