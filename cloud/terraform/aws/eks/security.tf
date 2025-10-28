resource "aws_security_group" "cluster" {
	name_prefix="${var.deployment_name}-cluster-sg"
	vpc_id=module.vpc.vpc_id
}
resource "aws_security_group" "nodes" {
	name_prefix="${var.deployment_name}-nodes-sg"
	vpc_id=module.vpc.vpc_id
}
resource "aws_vpc_security_group_egress_rule" "cluster_egress" {
	cidr_ipv4="0.0.0.0/0"
	ip_protocol="-1"
	security_group_id=aws_security_group.cluster.id
}
resource "aws_vpc_security_group_egress_rule" "nodes_egress" {
	cidr_ipv4="0.0.0.0/0"
	ip_protocol="-1"
	security_group_id=aws_security_group.nodes.id
}
resource "aws_vpc_security_group_ingress_rule" "cluster_ingress" {
	cidr_ipv4=var.vpc_cidr
	from_port=80
	ip_protocol="tcp"
	security_group_id=aws_security_group.cluster.id
	to_port=65535
}
resource "aws_vpc_security_group_ingress_rule" "nodes_ingress" {
	cidr_ipv4=var.vpc_cidr
	from_port=80
	ip_protocol="tcp"
	security_group_id=aws_security_group.nodes.id
	to_port=443
}
resource "aws_vpc_security_group_ingress_rule" "nodes_ingress_cluster_sg" {
	from_port=80
	ip_protocol="tcp"
	referenced_security_group_id=aws_security_group.cluster.id
	security_group_id=aws_security_group.nodes.id
	to_port=443
}
resource "aws_vpc_security_group_ingress_rule" "nodes_ingress_ephemeral" {
	cidr_ipv4=var.vpc_cidr
	from_port=1025
	ip_protocol="tcp"
	security_group_id=aws_security_group.nodes.id
	to_port=65535
}