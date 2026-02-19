#!/bin/bash
# Oracle Cloud 一键部署脚本
# 使用方法：curl -fsSL deploy.sh | bash

set -e

echo "🚀 开始部署企业微信医疗系统..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 检查是否以 root 运行
if [ "$EUID" -ne 0 ]; then
    log_error "请使用 sudo 运行此脚本"
    exit 1
fi

# 更新系统
log_info "更新系统..."
apt update && apt upgrade -y

# 安装基础工具
log_info "安装基础工具..."
apt install -y curl wget git vim unzip software-properties-common

# 安装 Java 17
log_info "安装 Java 17..."
apt install -y openjdk-17-jdk
java -version

# 安装 Node.js 20
log_info "安装 Node.js 20..."
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
apt install -y nodejs
node -v
npm -v

# 安装 Docker
log_info "安装 Docker..."
curl -fsSL https://get.docker.com | bash
systemctl enable docker
systemctl start docker

# 安装 Docker Compose
log_info "安装 Docker Compose..."
DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
mkdir -p $DOCKER_CONFIG/cli-plugins
curl -SL https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-linux-aarch64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose

# 安装 MySQL
log_info "安装 MySQL..."
debconf-set-selections <<< "mysql-server mysql-server/root_password password RootP@ssw0rd123!"
debconf-set-selections <<< "mysql-server mysql-server/root_password_again password RootP@ssw0rd123!"
apt install -y mysql-server
systemctl enable mysql
systemctl start mysql

# 安装 Redis
log_info "安装 Redis..."
apt install -y redis-server
systemctl enable redis
systemctl start redis

# 安装 Nginx
log_info "安装 Nginx..."
apt install -y nginx
systemctl enable nginx
systemctl start nginx

# 创建应用目录
log_info "创建应用目录..."
mkdir -p /opt/healthcare
cd /opt/healthcare

# 配置防火墙
log_info "配置防火墙..."
apt install -y ufw
ufw --force enable
ufw default deny incoming
ufw default allow outgoing
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 3306/tcp
ufw allow 6379/tcp

# 创建 MySQL 数据库
log_info "创建数据库..."
mysql -u root -pRootP@ssw0rd123! <<EOF
CREATE DATABASE IF NOT EXISTS healthcare_admin DEFAULT CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'healthcare'@'localhost' IDENTIFIED BY 'HealthP@ss123!';
GRANT ALL PRIVILEGES ON healthcare_admin.* TO 'healthcare'@'localhost';
FLUSH PRIVILEGES;
EOF

# 下载项目代码（从 Git 仓库）
# log_info "下载项目代码..."
# git clone <your-repo-url> /opt/healthcare/code

# 创建 docker-compose.yml
log_info "创建部署配置文件..."
# 这里应该从仓库复制 deploy 目录

# 创建环境变量文件
cat > /opt/healthcare/.env <<EOF
MYSQL_ROOT_PASSWORD=RootP@ssw0rd123!
MYSQL_USER=healthcare
MYSQL_PASSWORD=HealthP@ss123!
REDIS_PASSWORD=RedisP@ss123!
JWT_SECRET=change-this-to-a-random-secret-in-production
WECHAT_CORP_ID=wwace533e386c63f72
WECHAT_CORP_SECRET=your-corp-secret
WECHAT_AGENT_ID=1000007
WECHAT_TOKEN=your-token
WECHAT_ENCODING_AES_KEY=your-aes-key
EOF

# 设置权限
chmod 600 /opt/healthcare/.env

log_info "✅ 基础环境部署完成！"
echo ""
echo "📋 下一步："
echo "1. 上传项目代码到 /opt/healthcare/"
echo "2. 配置 .env 文件中的企业微信参数"
echo "3. 运行：cd /opt/healthcare && docker-compose up -d"
echo "4. 查看日志：docker-compose logs -f"
echo ""
echo "🔐 默认数据库信息："
echo "   Host: localhost"
echo "   Port: 3306"
echo "   Database: healthcare_admin"
echo "   User: healthcare"
echo "   Password: HealthP@ss123!"
