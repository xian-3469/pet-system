#!/bin/bash
# 宠物救助管理系统 一键启动脚本
# 用法: bash start.sh
# 依赖：便携版 JDK/Maven/MySQL/Redis 安装在 ~/tools 下（本机部署方式）；
#       标准安装环境下请自行调整 JAVA_HOME/MYSQL 路径

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
API_DIR="$BASE_DIR/backend"
VUE_DIR="$BASE_DIR/frontend"
export JAVA_HOME="$HOME/tools/jdk8u504-b01"
export PATH="$JAVA_HOME/bin:$HOME/tools/apache-maven-3.8.8/bin:$PATH"
export LD_LIBRARY_PATH="$HOME/tools/lib:$LD_LIBRARY_PATH"

port_up() { ss -tln 2>/dev/null | grep -q ":$1 "; }

# 1. Redis
if port_up 6379; then echo "[1/4] Redis 已在运行"; else
  setsid "$HOME/tools/redis-6.2.14/src/redis-server" --port 6379 >/tmp/redis_run.log 2>&1 < /dev/null &
  echo "[1/4] Redis 已启动"
fi

# 2. MySQL（便携版，数据目录 ~/tools/mysql-data）
if port_up 3306; then echo "[2/4] MySQL 已在运行"; else
  MYSQL_BASE=$(ls -d "$HOME"/tools/mysql-8.0.*-minimal 2>/dev/null | head -1)
  setsid "$MYSQL_BASE/bin/mysqld" --no-defaults --basedir="$MYSQL_BASE" --datadir="$HOME/tools/mysql-data" \
    --socket="$HOME/tools/mysql.sock" --port=3306 --bind-address=0.0.0.0 \
    --pid-file="$HOME/tools/mysql.pid" --tmpdir="$HOME/tools/mysql-tmp" >/tmp/mysql_run.log 2>&1 < /dev/null &
  for i in $(seq 1 30); do port_up 3306 && break; sleep 1; done
  echo "[2/4] MySQL 已启动"
fi

# 3. 后端（9311）
if port_up 9311; then echo "[3/4] 后端已在运行"; else
  cd "$API_DIR" && setsid nohup mvn -q spring-boot:run -DskipTests >/tmp/backend.log 2>&1 < /dev/null &
  echo "[3/4] 后端启动中（约 40 秒），日志 /tmp/backend.log"
fi

# 4. 前端（9312）
if port_up 9312; then echo "[4/4] 前端已在运行"; else
  cd "$VUE_DIR" && setsid nohup env NODE_OPTIONS=--openssl-legacy-provider npx vue-cli-service serve >/tmp/frontend.log 2>&1 < /dev/null &
  echo "[4/4] 前端启动中（约 40 秒），日志 /tmp/frontend.log"
fi

echo
echo "启动完成后访问："
echo "  本机（虚拟机内）：http://localhost:9312"
echo "  宿主机/其他电脑：http://$(hostname -I | awk '{print $1}'):9312"
echo "测试账号：admin/123456（管理员）、user1/123456（用户）"
