# 项目运行指南

## 环境要求

- Java 17+
- Maven 3.8+ (或使用IDE内置Maven)
- MySQL 8.0+
- Redis 7+
- Node.js 18+ (前端)

## 快速开始

### 方式1：使用IDE（推荐）

#### IntelliJ IDEA
1. 打开项目 `healthcare-admin`
2. 等待Maven依赖下载完成
3. 运行测试：右键 `src/test/java` → Run 'All Tests'
4. 启动应用：运行 `HealthcareAdminApplication.java`

#### VS Code
1. 安装扩展：Extension Pack for Java
2. 打开项目
3. 点击测试文件中的 "Run Test" 按钮

### 方式2：安装Maven

#### Windows (使用Chocolatey)
```powershell
choco install maven
```

#### Windows (使用Scoop)
```powershell
scoop install maven
```

#### 手动安装
1. 下载：https://maven.apache.org/download.cgi
2. 解压到 `C:\Program Files\Apache\maven`
3. 添加环境变量：
   - `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - `Path` 添加 `%MAVEN_HOME%\bin`
4. 验证：`mvn -version`

### 方式3：使用Docker

```powershell
# 编译项目
docker run -it --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean install

# 运行测试
docker run -it --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-17 mvn test

# 运行应用
docker run -it --rm -v "${PWD}:/app" -w /app -p 8081:8081 maven:3.9-eclipse-temurin-17 mvn spring-boot:run
```

## 常用命令

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 跳过测试打包
mvn clean package -DskipTests
```

## 前端运行

```bash
cd healthcare-admin-ui
npm install
npm run dev
```

## 数据库初始化

```bash
mysql -u root -p < deploy/sql/schema.sql
mysql -u root -p < deploy/sql/health_tables.sql
```

## 访问地址

- 后端API：http://localhost:8081
- API文档：http://localhost:8081/doc.html
- 前端页面：http://localhost:5175
- 默认账号：admin / admin123
