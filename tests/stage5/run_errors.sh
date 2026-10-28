#!/bin/bash
# Запуск по очереди всех скриптов с ошибочными вызовами rm и cp.
# Закрывайте окно эмулятора, чтобы перейти к следующему скрипту.
cd "$(dirname "$0")/../.."
for script in tests/stage5/errors/*.txt; do
    ./run.sh --vfs tests/vfs/deep.zip --script "$script"
done
