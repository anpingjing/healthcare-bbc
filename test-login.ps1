$headers = @{"Content-Type" = "application/json"}
$body = '{"wechatId":"test123456"}'
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Headers $headers -Body $body
Write-Host "Status: $($response.StatusCode)"
Write-Host "Response: $($response.Content)"
