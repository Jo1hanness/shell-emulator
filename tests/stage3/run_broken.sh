#!/bin/bash
# Запуск эмулятора с VFS: broken
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/broken.zip
