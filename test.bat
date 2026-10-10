@echo off
echo ========================================================
echo   FLASHDEAL PRO - Running Automated Concurrency and Unit Tests
echo ========================================================

call compile.bat
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Cannot run tests because compilation failed.
    exit /b 1
)

java -Dfile.encoding=UTF-8 -cp bin com.flashdeal.test.FlashSaleTestSuite
exit /b %ERRORLEVEL%
