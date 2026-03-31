@echo off
echo ========================================
echo   Drink Sales System - Compilation
echo ========================================
echo.

set POSTGRES_JAR=lib\postgresql-42.7.3.jar

if not exist bin mkdir bin

echo [1/5] Compiling common interfaces...
javac -d bin src\common\*.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation of common interfaces failed!
    pause
    exit /b %errorlevel%
)
echo Success!

echo [2/5] Compiling server...
javac -cp "bin;%POSTGRES_JAR%" -d bin src\server\*.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation of server failed!
    pause
    exit /b %errorlevel%
)
echo Success!

echo [3/5] Compiling client base...
javac -cp "bin;%POSTGRES_JAR%" -d bin src\client\BaseBranchGUI.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation of BaseBranchGUI failed!
    pause
    exit /b %errorlevel%
)
echo Success!

echo [4/5] Compiling branch clients...
javac -cp "bin;%POSTGRES_JAR%" -d bin src\client\NAIROBI.java
javac -cp "bin;%POSTGRES_JAR%" -d bin src\client\NAKURU.java
javac -cp "bin;%POSTGRES_JAR%" -d bin src\client\MOMBASA.java
javac -cp "bin;%POSTGRES_JAR%" -d bin src\client\KISUMU.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation of branch clients failed!
    pause
    exit /b %errorlevel%
)
echo Success!

echo [5/5] Compilation complete!
echo.
echo ========================================
echo   TO RUN THE SYSTEM:
echo ========================================
echo 1. Make sure PostgreSQL is running
echo 2. Run: run_server.bat
echo 3. Run: run_NAIROBI.bat (Headquarters)
echo 4. Run: run_NAKURU.bat, run_MOMBASA.bat, run_KISUMU.bat
echo ========================================
pause