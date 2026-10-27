#!/bin/bash
# Запуск эмулятора: script_only
cd "$(dirname "$0")/../.."
./run.sh --script tests/stage2/ok.txt
