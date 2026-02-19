# Deploy to Railway (Backend) & Vercel (Frontend)
# Usage: ./deploy_cli.ps1

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Healthcare BBC - Automated Deployment" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# ------------------------------------------------------------------
# 1. Prerequisites Check
# ------------------------------------------------------------------
Write-Host "`n[1/5] Checking tools..." -ForegroundColor Yellow
if (-not (Get-Command "npm" -ErrorAction SilentlyContinue)) {
    Write-Error "Node.js (npm) is not installed. Please install it first."
    exit 1
}

# Install Railway CLI if missing
if (-not (Get-Command "railway" -ErrorAction SilentlyContinue)) {
    Write-Host "Installing Railway CLI..."
    npm install -g @railway/cli
}

# Install Vercel CLI if missing
if (-not (Get-Command "vercel" -ErrorAction SilentlyContinue)) {
    Write-Host "Installing Vercel CLI..."
    npm install -g vercel
}

# ------------------------------------------------------------------
# 2. Deploy Backend to Railway
# ------------------------------------------------------------------
Write-Host "`n[2/5] Deploying Backend to Railway..." -ForegroundColor Yellow

# Login
Write-Host "Please login to Railway in the browser window that opens..."
railway login

# Init Project
Write-Host "`nInitialize Railway Project (Select 'Empty Project' or existing one):"
railway init

# Configure Environment Variables
$supabasePass = Read-Host "Enter your Supabase Database Password"
$jwtSecret = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 32 | % {[char]$_})

Write-Host "Setting Environment Variables..."
railway variables set `
    SPRING_DATASOURCE_URL="jdbc:postgresql://db.texbpopnfrjnkopnqjzd.supabase.co:5432/postgres?sslmode=require" `
    SPRING_DATASOURCE_USERNAME="postgres" `
    SPRING_DATASOURCE_PASSWORD="$supabasePass" `
    SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.postgresql.Driver" `
    JWT_SECRET="$jwtSecret" `
    PORT="8080"

# Deploy
Write-Host "`nDeploying code to Railway..."
railway up --detach

Write-Host "`n✅ Backend deployment started!" -ForegroundColor Green
Write-Host "Please go to your Railway Dashboard, find the deployed service, and copy its 'Public Domain' (e.g. https://xxx.up.railway.app)." -ForegroundColor Yellow
$railwayUrl = Read-Host "Paste the Railway Public Domain URL here (no trailing slash)"

# ------------------------------------------------------------------
# 3. Deploy Frontend to Vercel
# ------------------------------------------------------------------
Write-Host "`n[3/5] Deploying Frontend to Vercel..." -ForegroundColor Yellow
Set-Location "healthcare-bbc/frontend"

# Login
Write-Host "Please login to Vercel..."
vercel login

# Deploy
Write-Host "`nLinking and Deploying to Vercel..."
# Pull Vercel environment info (interactive)
vercel link

# Set Environment Variable for Frontend
Write-Host "Setting VITE_API_BASE_URL to $railwayUrl"
# We use a trick to pipe input to `vercel env add` if possible, but interactive is safer for first run.
# simpler: let's just write it to .env.production.local and deploy, Vercel will pick it up if we upload sources, 
# but Vercel CLI standard way is `vercel env add`.
# Let's try to pass it via build env or just ask user to set it in dashboard if CLI is too complex interactive.
# Actually, we can use --build-env for the deployment command.
vercel --prod --build-env VITE_API_BASE_URL="$railwayUrl"

Set-Location ../..

# ------------------------------------------------------------------
# 4. Finish
# ------------------------------------------------------------------
Write-Host "`n=========================================" -ForegroundColor Cyan
Write-Host "   Deployment Complete! 🚀" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Backend: $railwayUrl/actuator/health"
Write-Host "Frontend: (Check the Vercel output URL above)"
