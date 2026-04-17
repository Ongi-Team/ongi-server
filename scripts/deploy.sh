#!/bin/bash
set -e

BLUE_PORT=8080
GREEN_PORT=8081
NGINX_CONF="/etc/nginx/sites-available/default"
IMAGE="pooreumjung/ongi-backend:latest"
ENV_FILE="/home/ubuntu/app/.env.prod"

echo "=== Blue-Green Deploy Start ==="

# 0. Docker 네트워크 생성 (없으면)
docker network create ongi-net 2>/dev/null || true

# 1. Redis 실행 여부 확인
if ! docker ps | grep -q ongi-redis; then
  echo "Redis 컨테이너가 실행 중이 아닙니다. Redis를 먼저 실행합니다."
  docker run -d \
    --name ongi-redis \
    --network ongi-net \
    --restart unless-stopped \
    redis:7-alpine
fi

# 2. 현재 nginx가 바라보는 포트 확인
CURRENT_PORT=$(grep -oP 'set \$service_port \K[0-9]+' $NGINX_CONF | head -n 1)
if [ -z "$CURRENT_PORT" ]; then
  CURRENT_PORT=$BLUE_PORT
fi

if [ "$CURRENT_PORT" = "$BLUE_PORT" ]; then
  NEW_PORT=$GREEN_PORT
else
  NEW_PORT=$BLUE_PORT
fi

echo "현재 포트: $CURRENT_PORT"
echo "새 포트: $NEW_PORT"

# 3. 최신 이미지 pull
echo "이미지 pull 중..."
docker pull $IMAGE

# 4. 기존 NEW_PORT 컨테이너 제거
docker rm -f ongi-backend-${NEW_PORT} 2>/dev/null || true

# 5. 새 컨테이너 실행
echo "새 컨테이너 실행 (${NEW_PORT})"
docker run -d \
  --name ongi-backend-${NEW_PORT} \
  --network ongi-net \
  -p ${NEW_PORT}:8080 \
  --env-file ${ENV_FILE} \
  --restart unless-stopped \
  $IMAGE

# 6. 헬스체크 (최대 120초)
echo "헬스체크 확인 중..."
SUCCESS=false
for i in {1..24}
do
  if curl -fsS --connect-timeout 3 --max-time 5 http://localhost:${NEW_PORT}/actuator/health > /dev/null 2>&1; then
    SUCCESS=true
    echo "헬스체크 성공"
    break
  fi
  echo "헬스체크 재시도 ($i/24)"
  sleep 5
done

if [ "$SUCCESS" != "true" ]; then
  echo "헬스체크 최종 실패 → 롤백"
  docker logs --tail 50 ongi-backend-${NEW_PORT}
  docker stop ongi-backend-${NEW_PORT}
  docker rm ongi-backend-${NEW_PORT}
  exit 1
fi

# 7. nginx 포트 전환
echo "nginx 전환 중..."
sudo sed -i "s/set \$service_port .*/set \$service_port $NEW_PORT;/" $NGINX_CONF
sudo nginx -t
sudo systemctl reload nginx
echo "nginx 전환 완료"

# 8. 기존 컨테이너 종료
echo "기존 컨테이너 종료 (${CURRENT_PORT})"
docker stop ongi-backend-${CURRENT_PORT} 2>/dev/null || true
docker rm ongi-backend-${CURRENT_PORT} 2>/dev/null || true

# 9. 사용하지 않는 이미지 정리
docker image prune -f

echo "=== 배포 완료 (포트: $NEW_PORT) ==="