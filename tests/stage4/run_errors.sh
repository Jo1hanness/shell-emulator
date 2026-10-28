#!/bin/bash
# Запуск по очереди всех скриптов с ошибочными командами этапа 4.
# Закрывайте окно эмулятора, чтобы перейти к следующему скрипту.
cd "$(dirname "$0")/../.."
for script in tests/stage4/errors/*.txt; do
    ./run.sh --vfs tests/vfs/deep.zip --script "$script"
done
