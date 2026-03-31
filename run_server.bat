@echo off
echo ========================================
echo   Drink Sales System - RMI SERVER
echo ========================================
echo.
cd bin
java -cp ".;../lib/postgresql-42.7.3.jar" server.DrinkServer
pause