@echo off
echo Compiling Smart Expense Tracker...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files with dependencies
javac -cp "lib/*;src" -d bin src/com/expensetracker/main/*.java src/com/expensetracker/model/*.java src/com/expensetracker/dao/*.java src/com/expensetracker/gui/*.java src/com/expensetracker/service/*.java src/com/expensetracker/utils/*.java src/com/expensetracker/exceptions/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Run the application with: run.bat
) else (
    echo Compilation failed!
    pause
)
