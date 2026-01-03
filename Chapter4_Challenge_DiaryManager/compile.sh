#!/bin/bash
echo "Compiling Diary Manager..."
mkdir -p bin
javac -d bin -cp src src/diarymanager/*.java
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run: java -cp bin diarymanager.Main"
else
    echo "Compilation failed!"
fi