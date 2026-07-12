#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/spring-webapp.jar"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# 현재 구동 중인 애플리케이션 pid 확인
CURRENT_PID=$(pgrep -f $JAR_FILE)

# 프로세스가 켜져 있으면 종료
if [ -z "$CURRENT_PID" ]; then
  echo "$TIME_NOW > 현재 실행중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 실행중인 $CURRENT_PID 애플리케이션 종료 (SIGTERM)" >> $DEPLOY_LOG
  kill -15 $CURRENT_PID

  # 최대 45초 동안 프로세스가 실제로 종료될 때까지 대기 (Graceful shutdown 대기, appspec.yml 훅 timeout 60초보다 짧게 설정)
  for i in $(seq 1 45); do
    if ! kill -0 $CURRENT_PID 2>/dev/null; then
      echo "$(date +%c) > $CURRENT_PID 정상 종료 확인" >> $DEPLOY_LOG
      break
    fi
    sleep 1
  done

  # 타임아웃까지도 살아있으면 강제 종료
  if kill -0 $CURRENT_PID 2>/dev/null; then
    echo "$(date +%c) > $CURRENT_PID 강제 종료(SIGKILL)" >> $DEPLOY_LOG
    kill -9 $CURRENT_PID
  fi
fi