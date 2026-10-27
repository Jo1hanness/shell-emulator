#!/bin/bash
# Сборка и запуск эмулятора. Все аргументы передаются в Main:
#   ./run.sh [--vfs PATH] [--script PATH]
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/*.java && java -cp out Main "$@"
