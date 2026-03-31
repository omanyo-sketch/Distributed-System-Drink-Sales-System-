@echo off
echo ========================================
echo   NAKURU BRANCH
echo ========================================
echo.
cd bin
java -cp ".;../lib/postgresql-42.7.3.jar" client.NAKURU
pause