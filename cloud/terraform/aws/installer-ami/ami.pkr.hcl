build {
    name="builder"
    post-processor "manifest" {
        output="manifest.json"
        strip_path=true
    }

    #
    # Provisioner order matters
    #

    provisioner "shell" {
        inline=[
            "sudo mkdir --parents /opt/liferay/bin",
            "sudo mkdir --parents /opt/liferay/terraform",
            "sudo chown --recursive ec2-user:ec2-user /opt/liferay"
        ]
    }
    provisioner "file" {
        destination="/opt/liferay/terraform"
        source="../"
    }
    provisioner "shell" {
        environment_vars=[
            "DXP_AWS_CHART_VERSION=${var.dxp_aws_chart_version}",
            "DXP_IMAGE_TAG=${var.dxp_image_tag}",
        ]
        script="install_tools.sh"
    }
    provisioner "file" {
        destination="/opt/liferay/bin/run_on_boot.sh"
        source="run_on_boot.sh"
    }
    provisioner "file" {
        destination="/tmp/cloud_config.yaml"
        source="cloud_config.yaml"
    }
    provisioner "shell" {
        inline=[
            "sudo mv /tmp/cloud_config.yaml /etc/cloud/cloud.cfg.d/99-cloud-config.cfg",
            "tree -a /opt/liferay"
        ]
    }

    sources=[
        "source.amazon-ebs.this"
    ]
}
data "amazon-ami" "al2023_base" {
    filters={
        name="al2023-ami-2023.*-x86_64"
        root-device-type="ebs"
        virtualization-type="hvm"
    }
    most_recent=true
    owners=[
        "amazon"
    ]
    region=var.region
}
locals {
    build_time=formatdate("YYYY-MM-DD'T'HH-mm-ss'Z'", timestamp())
}
packer {
    required_plugins {
        amazon={
            source="github.com/hashicorp/amazon"
            version=">= 1.4.0"
        }
    }
}
source "amazon-ebs" "this" {
    ami_name="liferay-installer-ami-${local.build_time}"
    instance_type=var.instance_type
    region=var.region
    source_ami=data.amazon-ami.al2023_base.id
    ssh_username="ec2-user"
    ssh_clear_authorized_keys=true
    tags={
        "com.liferay.base-ami-id"=data.amazon-ami.al2023_base.id
        "com.liferay.base-ami-name"=data.amazon-ami.al2023_base.name
        "com.liferay.build-time"=local.build_time
        "com.liferay.dxp.aws-chart-version"=var.dxp_aws_chart_version == "" ? "used latest 'liferay-aws' at build time" : var.dxp_aws_chart_version
        "com.liferay.dxp.image-tag"=var.dxp_image_tag == "" ? "used latest 'lts', 'slim' at build time" : var.dxp_image_tag
        "com.liferay.vcs-url"="https://github.com/liferay/liferay-portal"
        "com.liferay.vcs-ref"=var.vcs_ref
        Name="Liferay DXP Cloud Native Installer AMI for AWS"
    }
}
variable "dxp_aws_chart_version" {
    default=""
}
variable "dxp_image_tag" {
    default=""
}
variable "instance_type" {
    default="t3.micro"
}
variable "region" {
    type=string
}
variable "vcs_ref" {
    default=""
}