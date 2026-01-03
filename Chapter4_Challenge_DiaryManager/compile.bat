@echo off
echo Compiling Diary Manager...
mkdir bin 2>nul
javac -d bin -cp src src\diarymanager\*.java
if %errorlevel% equ 0 (
    echo Compilation successful!
    echo.
    echo To run: java -cp bin diarymanager.Main
) else (
    echo Compilation failed!
)