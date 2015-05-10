#!/usr/bin/env bash
set -e

b=`tput bold`
nb=`tput sgr0`

UBUNTU_IMAGE_ID="9801950"
BIG_INSTANCE_SIZE="64"
SMALL_INSTANCE_SIZE="66"
CI_DROPLET_NAME="lambdacd-101"
APP_DROPLET_NAME="lambdacd-101-app"

# Keys to use to deploy app from lambdacd to app server.
# these are copied to the lambdacd-server so best don't
# use them for anything else.

# If these names are changed, make sure the ansible
# playbooks reflect this change
DEPLOY_PRIVATE_KEY=~/.ssh/deploy_key
DEPLOY_PUBLIC_KEY=~/.ssh/deploy_key.pub

function echob {
    echo "${b}${1}${nb}"
}

