$env:MAVEN_PROJECTBASEDIR = $PSScriptRoot
$wrapperJar = Join-Path $PSScriptRoot ".mvn\wrapper\maven-wrapper.jar"

if (Test-Path $wrapperJar) {
    Write-Host "Starting Maven..."
    java "-Dmaven.multiModuleProjectDirectory=$PSScriptRoot" -cp $wrapperJar org.apache.maven.wrapper.MavenWrapperMain $args
} else {
    Write-Host "Maven Wrapper jar not found"
    exit 1
}
