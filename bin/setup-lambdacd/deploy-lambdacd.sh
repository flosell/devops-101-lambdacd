#!/usr/bin/env bash

set -e

SCRIPT_DIR=$(dirname $0)


if [ -z $LAMBDACD_HOST ]; then
    echo "LAMBDACD_HOST environment variable not set, don't know where to deploy to"
    exit 1
fi

cd ${SCRIPT_DIR}
. ../_common.sh

cp ../../target/*-standalone.jar ../../pipeline-0-standalone.jar

if [ ! -f ../../pipeline-0-standalone.jar ]; then
    echo "No Pipeline-Jar, did you run lein uberjar?"
    exit 1
fi

echob "Starting deployment of LambdaCD to $LAMBDACD_HOST"

if [ "$LAMBDACD_HOST" == "localhost" ]; then
    C="local"
else
    C="ssh"
    OPTIONAL_KEY="--private-key $DEPLOY_PRIVATE_KEY"
fi

ansible-playbook  -i "${LAMBDACD_HOST}," -c ${C} ${OPTIONAL_KEY} -u root deploy-lambdacd.yml
