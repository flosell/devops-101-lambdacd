#!/usr/bin/env bash
set -e

b=`tput bold`
nb=`tput sgr0`
SCRIPT_DIR=$(dirname $0)
AWS_PRIVATE_KEY="$HOME/.ssh/aws-main.cer"

function echob {
    echo "${b}${1}${nb}"
}

echob "Checking Prerequisites"
if ! type "aws" > /dev/null 2>&1; then
  echo "AWS CLI is not installed. Instructions here: http://docs.aws.amazon.com/cli/latest/userguide/installing.html"
  exit 1
fi

if ! type "ansible" > /dev/null 2>&1; then
  echo "Ansible is not installed. Instructions here: http://docs.ansible.com/intro_installation.html"
  exit 1
fi

if ! aws cloudformation describe-stacks > /dev/null 2>&1; then
  echo "Ansible is not installed. Instructions here: http://docs.ansible.com/intro_installation.html"
  exit 1
fi

if [ ! -f ${AWS_PRIVATE_KEY} ]; then
    echo "Expected to find a private key to ssh into EC2 instances at ${AWS_PRIVATE_KEY}"
    exit 1
fi

cd ${SCRIPT_DIR} # Just to have a directory for relative paths
echob "Starting up CI infrastructure using CloudFormation"

STACK_ID=$(aws cloudformation create-stack --stack-name infrastructure --template-body "file://../cloudformation/infrastructure-template.json" --capabilities CAPABILITY_IAM)
echo "Waiting for stack (${STACK_ID}) to become ready"

STACK_STATUS=""

while [ "${STACK_STATUS}" != "CREATE_COMPLETE" ]; do
    sleep 1
    STACK_STATUS=$(aws cloudformation describe-stacks --stack-name ${STACK_ID} | grep STACKS | cut -f 7)
    echo "Status of stack: ${STACK_STATUS}"
done

CI_SERVER_IP=$(aws cloudformation describe-stacks --stack-name ${STACK_ID} | grep CIMasterPublicIp | cut -f 3)

echo "CI Infrastructure is up, Server IP is ${CI_SERVER_IP}"
echo

echob "Compiling initial pipeline"
pushd ..

lein uberjar
cp target/*-standalone.jar pipeline-0-standalone.jar # Ansible playbook is looking for this file to upload

popd
echo
echob "Provisioning CI server"

echo -e "[ci-master]\n${CI_SERVER_IP}" > inventory
ansible-playbook ../ansible/playbook.yml -u ubuntu -i inventory --private-key=${AWS_PRIVATE_KEY}

echo
echo
echob "==============================================="
echob "SUCCESS: CI Environment is up and running at http://${CI_SERVER_IP}:8080. Your application is deploying there right now"