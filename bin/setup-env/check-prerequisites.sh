#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(dirname $0)

cd ${SCRIPT_DIR}

. ../_common.sh

function key_present {
  tugboat keys | grep "^$1 (id:" > /dev/null 2>&1
}

echob "Checking Prerequisites..."
if ! type "tugboat" > /dev/null 2>&1; then
  echo "Tugboat not installed. Instructions here: https://github.com/pearkes/tugboat"
  exit 1
fi

if ! tugboat verify > /dev/null 2>&1; then
  echo "Tugboat not configured correctly. Call tugboat verify for details"
  exit 1
fi

if ! type "ansible" > /dev/null 2>&1; then
  echo "Ansible is not installed. Instructions here: http://docs.ansible.com/intro_installation.html"
  exit 1
fi

if [ ! -f ${DEPLOY_PRIVATE_KEY} ]; then
  echo "Private key for deployment not found at ${DEPLOY_PRIVATE_KEY}. Make sure this is a throwaway-key as it will travel to the CI server"
  exit 1
fi

if [ ! -f ${DEPLOY_PUBLIC_KEY} ]; then
  echo "Private key for deployment not found at ${DEPLOY_PUBLIC_KEY}."
  exit 1
fi

if ! key_present "deploy"; then
  echo "DigitalOcean does not know a key named deploy. This needs to be assigned to your deployment-key"
  exit 1
fi

echob "Everything seems to be fine, you are ready to go!"
echo