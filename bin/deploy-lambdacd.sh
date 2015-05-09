#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $0)

if [ $# -ne 1 ]; then
    echo "Usage: $0 <host-to-deploy-to>"
    exit 1
fi

HOST=$1

cd ${SCRIPT_DIR}

if [ ! -f ../pipeline-0-standalone.jar ]; then
    echo "No Pipeline-Jar, did you run lein uberjar?"
    exit 1
fi

ansible-playbook  -i "${HOST}," -u root ../ansible/deploy-lambdacd.yml

