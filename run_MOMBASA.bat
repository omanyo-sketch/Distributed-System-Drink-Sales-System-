@echo off
echo ========================================
echo   MOMBASA BRANCH
echo ========================================
echo.
cd bin
java -cp ".;../lib/postgresql-42.7.3.jar" client.MOMBASA
pause