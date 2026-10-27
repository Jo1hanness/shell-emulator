#!/bin/bash
# Запуск эмулятора с VFS: empty
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/empty.zip --script tests/stage3/all.txt
