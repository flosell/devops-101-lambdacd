#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(dirname $0)
cd ${SCRIPT_DIR}
. _common.sh
./check-prerequisites.sh

function droplet_present {
  tugboat info --name $1 > /dev/null 2>&1
}

function deploy_key_id {
  tugboat keys | grep "^deploy (id:" | sed -e 's/.*id: \([0-9]*\))/\1/g'
}

function droplet_ip {
  tugboat info --name $1 | grep "IP:" | awk '{print $2}'
}


if ! droplet_present ${APP_DROPLET_NAME}; then
  echob "Spinning up new instance to hold your application"
  DEPLOY_KEY_ID=$(deploy_key_id)
  tugboat create ${APP_DROPLET_NAME} --image ${UBUNTU_IMAGE_ID} --size ${SMALL_INSTANCE_SIZE} --keys ${DEPLOY_KEY_ID}
  tugboat wait --name ${APP_DROPLET_NAME}
fi

APP_DROPLET_IP=$(droplet_ip ${APP_DROPLET_NAME})
echob "Droplet ${APP_DROPLET_NAME} ready to go at ${APP_DROPLET_IP}"

echob "Provisioning App Server..."
ansible-playbook  --inventory "${APP_DROPLET_IP}," --private-key ${DEPLOY_PRIVATE_KEY} --user root ../ansible/app-playbook.yml


if ! droplet_present ${CI_DROPLET_NAME}; then
  echob "Spinning up new instance to hold LambdaCD"
  tugboat create ${CI_DROPLET_NAME} --image ${UBUNTU_IMAGE_ID} --size ${BIG_INSTANCE_SIZE}
  tugboat wait --name ${CI_DROPLET_NAME}
fi

CI_DROPLET_IP=$(droplet_ip ${CI_DROPLET_NAME})
echob "Droplet ${CI_DROPLET_NAME} ready to go at ${CI_DROPLET_IP}"
echob

echob "Provisioning CI Server..."
ansible-playbook  --inventory "${CI_DROPLET_IP}," -u root --extra-vars "app_server_ip=${APP_DROPLET_IP}" ../ansible/ci-playbook.yml

echob
echob "Your CI Machine is provisioned successfully."


echob "Success!"
echob "=============================================="
echob
echob "App-Server:\t $APP_DROPLET_IP"
echob "CI-Server: \t $CI_DROPLET_IP"

