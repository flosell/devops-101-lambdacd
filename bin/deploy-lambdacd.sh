#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $0)


if [ -z $LAMBDACD_HOST ]; then
    echo "LAMBDACD_HOST environment variable not set, don't know where to deploy to"
    exit 1
fi

cd ${SCRIPT_DIR}

mv ../target/*-standalone.jar ../pipeline-0-standalone.jar

if [ ! -f ../pipeline-0-standalone.jar ]; then
    echo "No Pipeline-Jar, did you run lein uberjar?"
    exit 1
fi

echo "Starting deployment of LambdaCD to $LAMBDACD_HOST"

ansible-playbook  -i "${LAMBDACD_HOST}," -u root ../ansible/deploy-lambdacd.yml

