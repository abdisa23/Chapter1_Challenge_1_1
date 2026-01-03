@echo off
echo Building EcoLife Weather Widget...

REM Clean previous builds
call mvn clean

REM Compile
echo Compiling...
call mvn compile

REM Create executable JAR
echo Creating JAR...
call mvn package

REM Create distribution folder
if exist dist rmdir /s /q dist
mkdir dist
copy target\*.jar dist\
xcopy screenshots dist\screenshots /E /I
copy README.md dist\
copy INSTALLATION.md dist\

echo Build complete! Files are in the 'dist' folder.
echo To run: java -jar dist\BrandedWeatherWidget-1.0.0.jar
pause