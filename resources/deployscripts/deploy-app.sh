#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $0)


cd ${SCRIPT_DIR}

mv target/*-standalone.jar app.jar

echo "Starting deployment"

ansible-playbook  -i "app-server," -u root deploy-app.yml

