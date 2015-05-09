#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(dirname $0)
cd ${SCRIPT_DIR}
. _common.sh
export DIGITALOCEAN_TOKEN=""
./check-prerequisites.sh

