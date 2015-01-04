#!/usr/bin/env bash

PIPELINE_JAR_PATTERN="pipeline-*-standalone.jar"
while true; do
    CURRENT_JAR=$(ls $PIPELINE_JAR_PATTERN | sort | tail -n 1)
    echo "Starting Pipeline ${CURRENT_JAR}..."
    java -jar ${CURRENT_JAR}

    echo "Pipeline stopped. Restarting in 1 sec..."
    sleep 1
done