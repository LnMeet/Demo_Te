# MySQL 连接问题解决方案

## 问题诊断结果
- **错误代码**: SQL State 08001
- **问题**: Connection refused - MySQL 服务未运行

## 解决方案

### 1. 检查是否安装了 MySQL

```bash
# 检查 MySQL 是否安装
which mysql
which mysqld

# 检查系统服务
systemctl list-units | grep mysql
service --status-all 2>/dev/null | grep mysql
```

### 2. 安装 MySQL（如果未安装）

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install -y mysql-server
```

#### CentOS/RHEL:
```bash
sudo yum install -y mysql-server
```

#### 使用 Docker（推荐用于开发）:
```bash
# 拉取并运行 MySQL 8.0 容器
docker run -d \
  --name mysql-dev \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=test_db \
  mysql:8.0

# 查看容器状态
docker ps
```

### 3. 启动 MySQL 服务

#### 使用 systemd:
```bash
sudo systemctl start mysql
sudo systemctl enable mysql  # 开机自启
sudo systemctl status mysql
```

#### 使用 service:
```bash
sudo service mysql start
sudo service mysql status
```

### 4. 验证连接

```bash
# 使用命令行客户端测试
mysql -u root -p

# 或者重新运行我们的网络检查
java NetworkCheck

# 运行 JDBC 调试程序
java -cp .:mysql-connector-java-8.0.x.jar JdbcConnectionDebug
```

### 5. 创建测试数据库和表（如果需要）

```sql
-- 连接到 MySQL 后执行
CREATE DATABASE IF NOT EXISTS test_db;
USE test_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

INSERT INTO users (name, email) VALUES ('Test User', 'test@example.com');
```

## 快速 Docker 方案（最简单）

如果你有 Docker，这是最快的方式：

```bash
# 1. 启动 MySQL 容器
docker run -d \
  --name mysql8 \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=test_db \
  mysql:8.0

# 2. 等待几秒让 MySQL 启动
sleep 10

# 3. 测试连接
java NetworkCheck

# 4. 更新 JdbcMySqlExample.java 中的密码为 root123 并运行
```
