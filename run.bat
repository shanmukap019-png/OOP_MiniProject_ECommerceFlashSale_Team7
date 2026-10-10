@echo off
echo ========================================================
echo   FLASHDEAL PRO - Starting Desktop Application
echo ========================================================

if not exist bin (
    echo [INFO] Bin directory not found. Compiling first...
    call compile.bat
)

java -Dfile.encoding=UTF-8 -cp bin com.flashdeal.app.Main
