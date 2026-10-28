#!/bin/bash
# Тест команд этапа 4 на многоуровневой VFS
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/deep.zip --script tests/stage4/commands.txt
