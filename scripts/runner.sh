#!/usr/bin/env bash


SCRIPT_DIR="$(dirname "$0")"
LOGFILE="${SCRIPT_DIR}/pipeline-process.log"
PIDFILE="${SCRIPT_DIR}/pipeline.pid"

export LAMBDACD_HOST="localhost"

function run() {
  trap 'kill -TERM $PID; exit' SIGHUP SIGINT SIGTERM
  PIPELINE_JAR_PATTERN="pipeline-*-standalone.jar"
  while true; do
    CURRENT_JAR=$(ls $PIPELINE_JAR_PATTERN | sort | tail -n 1)
    echo "Starting Pipeline ${CURRENT_JAR}..."
    java -jar ${CURRENT_JAR} &
    PID=$!
    wait $PID
    echo "Pipeline stopped. Restarting in 1 sec..."
    sleep 1
  done
}

cd ${SCRIPT_DIR}

run </dev/null > "${SCRIPT_DIR}/pipeline-process.log" 2>&1 &
LOOP_PID=$!
echo $LOOP_PID > $PIDFILE
disown