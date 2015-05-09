#!/usr/bin/env bash
set -e

b=`tput bold`
nb=`tput sgr0`

UBUNTU_IMAGE_ID="9801950"
DROPLET_NAME="lambdacd-101"

function echob {
    echo "${b}${1}${nb}"
}

