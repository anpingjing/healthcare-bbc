@echo off
echo ========================================
echo  Healthcare Admin - 项目构建脚本
echo ========================================
echo.

REM 检查Maven是否安装
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Maven已安装
    mvn -version
    echo.
    echo [INFO] 开始执行Maven命令: %*
    mvn %*
) else (
    echo [ERROR] Maven未安装！
    echo.
    echo 请选择以下方式之一：
    echo.
    echo 方式1: 安装Maven
    echo   1. 访问 https://maven.apache.org/download.cgi
    echo   2. 下载 apache-maven-3.9.x-bin.zip
    echo   3. 解压到 C:\Program Files\Apache\maven
    echo   4. 添加环境变量 MAVEN_HOME 和 Path
    echo   5. 重新打开终端后运行此脚本
    echo.
    echo 方式2: 使用IDE
    echo   - IntelliJ IDEA: 右键项目 -^> Run -^> All Tests
    echo   - VS Code: 安装 "Java Extension Pack" 扩展
    echo.
    echo 方式3: 使用Docker
    echo   docker run -it --rm -v "%cd%":/app -w /app maven:3.9-eclipse-temurin-17 mvn %*
    echo.
    pause
)
