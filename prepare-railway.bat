@echo off
REM Railway 部署准备脚本
REM 使用方法：双击运行或命令行执行 prepare-railway.bat

echo ========================================
echo   Railway 部署准备脚本
echo   企业微信医疗管理系统
echo ========================================
echo.

REM 检查 git 是否安装
where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到 Git，请先安装 Git
    echo 下载地址：https://git-scm.com/download/win
    pause
    exit /b 1
)

echo [1/5] 检查 Git 安装... OK
echo.

REM 检查是否在正确的目录
if not exist "healthcare-bbc" (
    echo [错误] 未找到 healthcare-bbc 目录
    echo 请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo [2/5] 检查项目结构... OK
echo.

REM 初始化 git 仓库（如果还没有）
if not exist ".git" (
    echo [3/5] 初始化 Git 仓库...
    git init
    echo.
) else (
    echo [3/5] Git 仓库已存在... OK
    echo.
)

REM 创建 .gitignore
if not exist ".gitignore" (
    echo [4/5] 创建 .gitignore...
    (
        echo node_modules/
        echo target/
        echo *.log
        echo .env
        echo .env.local
        echo .env.production
        echo .DS_Store
        echo .idea/
        echo .vscode/
        echo deploy/.env
        echo deploy/mysql-init/
    ) > .gitignore
    echo.
) else (
    echo [4/5] .gitignore 已存在... OK
    echo.
)

REM 添加并提交文件
echo [5/5] 添加并提交文件...
git add .
git commit -m "Prepare for Railway deployment"

echo.
echo ========================================
echo   准备完成！
echo ========================================
echo.
echo 下一步：
echo 1. 创建 GitHub 仓库：https://github.com/new
echo 2. 关联远程仓库:
echo    git remote add origin https://github.com/你的用户名/healthcare-bbc.git
echo    git branch -M main
echo    git push -u origin main
echo.
echo 3. 访问 Railway: https://railway.com/
echo 4. 按照 RAILWAY.md 文档完成部署
echo.
echo 文档位置：deploy\RAILWAY.md
echo.

pause
