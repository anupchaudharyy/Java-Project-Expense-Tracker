@echo off
echo Starting Smart Expense Tracker...

REM Start the Python AI server in the background and log output
echo ========================================
echo Starting AI server in the background...
echo Output will be logged to python_server.log
echo ========================================
start /b python python/ollama_server.py > python_server.log 2>&1

REM Wait for a few seconds to ensure the server starts
echo.
echo ========================================
echo Waiting for AI server to initialize...
echo Please wait for about 10 seconds.
echo ========================================
timeout /t 10 /nobreak >nul

REM Run the Java application with all dependencies
echo.
echo ========================================
echo Starting Smart Expense Tracker application...
echo ========================================
java -cp "bin;lib/*" com.expensetracker.main.ExpenseTrackerApp

pause