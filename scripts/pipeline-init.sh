#!/usr/bin/env bash

SERVICE_NAME=pipeline
DAEMON="/opt/pipeline/start-and-restart.sh"
DIR="/opt/pipeline"
USER="pipeline"
DAEMON_OPTS=""
PIDFILE="/var/run/pipeline.pid"

if [ ! -x $DAEMON ]; then
  echo "ERROR: Can't execute $DAEMON."
  exit 1
fi

start_service() {
  echo -n " * Starting $SERVICE_NAME... "
  start-stop-daemon --make-pidfile --background --chdir $DIR --chuid $USER -Sq -p $PIDFILE -x $DAEMON -- $DAEMON_OPTS
  e=$?
  if [ $e -eq 1 ]; then
    echo "already running"
    return
  fi

  if [ $e -eq 255 ]; then
    echo "couldn't start :("
    exit 1
  fi

  echo "done"
}

stop_service() {
  echo -n " * Stopping $SERVICE_NAME... "
  start-stop-daemon -Kq -R 10 -p $PIDFILE
  e=$?
  if [ $e -eq 1 ]; then
    echo "not running"
    return
  fi

  echo "done"
}

status_service() {
    printf "%-50s" "Checking $SERVICE_NAME..."
    if [ -f $PIDFILE ]; then
        PID=`cat $PIDFILE`
        if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
            printf "%s\n" "Process dead but pidfile exists"
            exit 1
        else
            echo "Running"
        fi
    else
        printf "%s\n" "Service not running"
        exit 3
    fi
}

case "$1" in
  status)
    status_service
    ;;
  start)
    start_service
    ;;
  stop)
    stop_service
    ;;
  restart)
    stop_service
    start_service
    ;;
  *)
    echo "Usage: service $SERVICE_NAME {start|stop|restart|status}" >&2
    exit 1
    ;;
esac

exit 0