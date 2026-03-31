@echo off
echo ========================================
echo   HEADQUARTERS - NAIROBI BRANCH
echo ========================================
echo.
cd bin
java -cp ".;../lib/postgresql-42.7.3.jar" client.NAIROBI
pause