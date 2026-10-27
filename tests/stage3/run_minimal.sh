#!/bin/bash
# Запуск эмулятора с VFS: minimal
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/minimal.zip --script tests/stage3/all.txt
