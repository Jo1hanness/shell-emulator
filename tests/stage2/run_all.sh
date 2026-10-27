#!/bin/bash
# Запуск эмулятора: all
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/minimal.zip --script tests/stage2/start.txt
