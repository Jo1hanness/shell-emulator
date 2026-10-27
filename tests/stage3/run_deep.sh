#!/bin/bash
# Запуск эмулятора с VFS: deep
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/deep.zip --script tests/stage3/all.txt
