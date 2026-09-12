#!/bin/bash
# 宠物救助管理系统 一键关闭脚本
# 用法: bash stop.sh
# 依次关闭：前端 → 后端 → MySQL → Redis

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
export LD_LIBRARY_PATH="$HOME/tools/lib:$LD_LIBRARY_PATH"

port_up() { ss -tln 2>/dev/null | grep -q ":$1 "; }

# 1. 前端（9312）
if port_up 9312; then
  pkill -f "vue-cli-service" 2>/dev/null
  echo "[1/4] 前端已关闭"
else
  echo "[1/4] 前端未在运行"
fi

# 2. 后端（9311）
if port_up 9311; then
  pkill -9 -f "PetManagerApplication" 2>/dev/null
  pkill -9 -f "spring-boot:run" 2>/dev/null
  for i in $(seq 1 10); do port_up 9311 || break; sleep 1; done
  echo "[2/4] 后端已关闭"
else
  echo "[2/4] 后端未在运行"
fi

# 3. MySQL（优先优雅关闭，保证数据落盘）
if port_up 3306; then
  MYSQL_BASE=$(ls -d "$HOME"/tools/mysql-8.0.*-minimal 2>/dev/null | head -1)
  "$MYSQL_BASE/bin/mysqladmin" --no-defaults -h 127.0.0.1 -P 3306 -u root -p123456 shutdown 2>/dev/null \
    || pkill -f "mysqld" 2>/dev/null
  for i in $(seq 1 15); do port_up 3306 || break; sleep 1; done
  echo "[3/4] MySQL 已关闭"
else
  echo "[3/4] MySQL 未在运行"
fi

# 4. Redis
if port_up 6379; then
  "$HOME/tools/redis-6.2.14/src/redis-cli" shutdown 2>/dev/null || pkill -f "redis-server" 2>/dev/null
  echo "[4/4] Redis 已关闭"
else
  echo "[4/4] Redis 未在运行"
fi

echo
echo "全部服务已关闭。下次启动：bash start.sh"
