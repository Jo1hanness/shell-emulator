#!/bin/bash
# Запуск эмулятора с VFS: missing
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/no_such_file.zip
