#!/bin/bash
# Запуск эмулятора: vfs_only
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/minimal.zip
