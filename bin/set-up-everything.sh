#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(dirname $0)
cd ${SCRIPT_DIR}
. _common.sh
./check-prerequisites.sh

function droplet_not_present {
    tugboat droplets | grep -v $1 > /dev/null 2>&1
}

if droplet_not_present ${CI_DROPLET_NAME}; then
  echob "Spinning up new instance to hold LambdaCD"
  tugboat create ${CI_DROPLET_NAME} --image ${UBUNTU_IMAGE_ID}
  tugboat wait ${CI_DROPLET_NAME}
fi

CI_DROPLET_IP=$(tugboat info ${CI_DROPLET_NAME} | grep "IP:" | awk '{print $2}')
echob "Droplet ${CI_DROPLET_NAME} ready to go at ${CI_DROPLET_IP}"
echob

echob "Provisioning CI Server..."
ansible-playbook  -i "${CI_DROPLET_IP}," -u root ../ansible/playbook.yml

echob
echob "Your CI Machine is provisioned successfully."