@echo off
echo ========================================================
echo   FLASHDEAL PRO - Compiling Java Source Files
echo ========================================================

if not exist bin mkdir bin
if not exist data mkdir data

powershell -NoProfile -ExecutionPolicy Bypass -Command "$src = Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName; javac -encoding UTF-8 -d bin $src"
set COMPILE_STATUS=%ERRORLEVEL%

if %COMPILE_STATUS% EQU 0 (
    echo [SUCCESS] Compilation finished successfully. Classes are in bin\
) else (
    echo [ERROR] Compilation failed with error code %COMPILE_STATUS%
)

exit /b %COMPILE_STATUS%
