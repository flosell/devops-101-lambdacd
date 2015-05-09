#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(dirname $0)

cd ${SCRIPT_DIR}

. _common.sh

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

echob "Everything seems to be fine, you are ready to go!"
echo
