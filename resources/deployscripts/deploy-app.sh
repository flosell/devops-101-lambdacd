#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $0)


cd ${SCRIPT_DIR}

mv target/uberjar/*-standalone.jar app.jar

export ANSIBLE_HOST_KEY_CHECKING=False

echo "Starting deployment"
ansible-playbook  -i "app-server," -u root deploy-app.yml

