#!/bin/sh
# Start Nginx in background
nginx

# Start Java application in foreground
exec java -jar app.jar
