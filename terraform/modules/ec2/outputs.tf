output "launch_template_id" {
  description = "ID of the launch template"
  value       = aws_launch_template.main.id
}

output "launch_template_arn" {
  description = "ARN of the launch template"
  value       = aws_launch_template.main.arn
}

output "autoscaling_group_name" {
  description = "Name of the Auto Scaling Group"
  value       = aws_autoscaling_group.main.name
}

output "autoscaling_group_arn" {
  description = "ARN of the Auto Scaling Group"
  value       = aws_autoscaling_group.main.arn
}

output "instance_role_name" {
  description = "Name of the IAM role attached to EC2 instances"
  value       = aws_iam_role.ec2_role.name
}

output "instance_role_arn" {
  description = "ARN of the IAM role attached to EC2 instances"
  value       = aws_iam_role.ec2_role.arn
}

output "instance_profile_name" {
  description = "Name of the IAM instance profile"
  value       = aws_iam_instance_profile.ec2_profile.name
}

output "instance_profile_arn" {
  description = "ARN of the IAM instance profile"
  value       = aws_iam_instance_profile.ec2_profile.arn
}

output "scale_up_policy_arn" {
  description = "ARN of the scale up policy"
  value       = aws_autoscaling_policy.scale_up.arn
}

output "scale_down_policy_arn" {
  description = "ARN of the scale down policy"
  value       = aws_autoscaling_policy.scale_down.arn
}

output "cloudwatch_alarms" {
  description = "Map of CloudWatch alarms for Auto Scaling"
  value = {
    high_cpu = {
      name = aws_cloudwatch_metric_alarm.high_cpu.alarm_name
      arn  = aws_cloudwatch_metric_alarm.high_cpu.arn
    }
    low_cpu = {
      name = aws_cloudwatch_metric_alarm.low_cpu.alarm_name
      arn  = aws_cloudwatch_metric_alarm.low_cpu.arn
    }
  }
}

output "asg_security_group_ids" {
  description = "List of security group IDs used by the Auto Scaling Group"
  value       = var.security_group_ids
}

output "asg_subnet_ids" {
  description = "List of subnet IDs used by the Auto Scaling Group"
  value       = var.subnet_ids
}

output "asg_configuration" {
  description = "Auto Scaling Group configuration details"
  value = {
    min_size         = var.min_size
    max_size         = var.max_size
    desired_capacity = var.desired_capacity
    instance_type    = var.instance_type
    ami_id           = var.ami_id
  }
}

output "scaling_metrics" {
  description = "List of metrics being monitored for Auto Scaling"
  value       = aws_autoscaling_group.main.enabled_metrics
}