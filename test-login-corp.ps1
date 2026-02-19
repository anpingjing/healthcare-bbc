$headers = @{"Content-Type" = "application/json"}
$body = '{"wechatId":"wwace533e386c63f72"}'
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Headers $headers -Body $body -UseBasicParsing
Write-Host "Status: $($response.StatusCode)"
Write-Host "Response: $($response.Content)"
