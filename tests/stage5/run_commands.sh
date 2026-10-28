#!/bin/bash
# Тест команд rm и cp на многоуровневой VFS
cd "$(dirname "$0")/../.."
./run.sh --vfs tests/vfs/deep.zip --script tests/stage5/commands.txt
