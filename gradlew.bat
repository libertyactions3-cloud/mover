@echo off
setlocal
set "GRADLE_VERSION=9.5.1"
set "GRADLE_BASE=%USERPROFILE%\.gradle\wrapper\dists\mover"
set "GRADLE_HOME=%GRADLE_BASE%\gradle-%GRADLE_VERSION%"

if not exist "%GRADLE_HOME%\bin\gradle.bat" (
    echo Downloading Gradle %GRADLE_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $version='%GRADLE_VERSION%'; $base=Join-Path $env:USERPROFILE '.gradle\wrapper\dists\mover'; $gradleHome=Join-Path $base ('gradle-' + $version); if (!(Test-Path (Join-Path $gradleHome 'bin\gradle.bat'))) { New-Item -ItemType Directory -Force -Path $base | Out-Null; $zip=Join-Path $env:TEMP ('gradle-' + $version + '-bin.zip'); Invoke-WebRequest -UseBasicParsing -Uri ('https://services.gradle.org/distributions/gradle-' + $version + '-bin.zip') -OutFile $zip; Expand-Archive -LiteralPath $zip -DestinationPath $base -Force; Remove-Item -LiteralPath $zip -Force }"
    if errorlevel 1 exit /b 1
)

call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %errorlevel%
