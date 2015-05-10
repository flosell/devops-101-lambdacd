#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(dirname $0)
cd ${SCRIPT_DIR}
. ../_common.sh
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

DEPLOY_KEY_ID=$(deploy_key_id)

function start_droplet {
  NAME=$1
  INSTANCE_SIZE=$2
  if ! droplet_present ${NAME}; then
    echob "Spinning up new instance ${NAME}"
    tugboat create ${NAME} --image ${UBUNTU_IMAGE_ID} --size ${INSTANCE_SIZE} --keys ${DEPLOY_KEY_ID}
    tugboat wait --name ${NAME}
  fi

  DROPLET_IP=$(droplet_ip ${APP_DROPLET_NAME})

  echo -n "Waiting for ssh to become available"

  ssh-keygen -R ${DROPLET_IP} > /dev/null 2>&1;
  while ! ssh -i ~/.ssh/deploy_key root@${DROPLET_IP}  exit > /dev/null 2>&1; do
    echo -n "."
    sleep 1
  done
}

start_droplet ${APP_DROPLET_NAME} ${SMALL_INSTANCE_SIZE}
APP_DROPLET_IP=$(droplet_ip ${APP_DROPLET_NAME})

echob "Droplet ${APP_DROPLET_NAME} ready to go at ${APP_DROPLET_IP}"

echob "Provisioning App Server..."
ansible-playbook  --inventory "${APP_DROPLET_IP}," --private-key ${DEPLOY_PRIVATE_KEY} --user root app-playbook.yml



start_droplet ${CI_DROPLET_NAME} ${BIG_INSTANCE_SIZE}
CI_DROPLET_IP=$(droplet_ip ${CI_DROPLET_NAME})
echob "Droplet ${CI_DROPLET_NAME} ready to go at ${CI_DROPLET_IP}"
echob

echob "Provisioning CI Server..."
ansible-playbook  --inventory "${CI_DROPLET_IP}," --private-key ${DEPLOY_PRIVATE_KEY} -u root --extra-vars "app_server_ip=${APP_DROPLET_IP}" ci-playbook.yml

echob
echob "Your CI Machine is provisioned successfully."


echob "Success!"
echob "=============================================="
echob
echob "App-Server: $APP_DROPLET_IP"
echob "CI-Server:  $CI_DROPLET_IP"

