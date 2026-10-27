"""Генерирует тестовые VFS-архивы для эмулятора: python3 tests/vfs/make_vfs.py"""
import os
import zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
# 1x1 PNG — двоичный файл для проверки хранения данных в base64
PNG = bytes.fromhex(
    "89504e470d0a1a0a0000000d4948445200000001000000010806000000"
    "1f15c4890000000d49444154789c63000100000500010d0a2db40000000049454e44ae426082")


def make(name, entries):
    with zipfile.ZipFile(os.path.join(HERE, name), "w", zipfile.ZIP_DEFLATED) as z:
        for path, data in entries:
            if path.endswith("/"):
                z.writestr(zipfile.ZipInfo(path), b"")
            else:
                z.writestr(path, data)


# Минимальная VFS: один файл в корне
make("minimal.zip", [("readme.txt", "minimal vfs\n")])

# Многоуровневая VFS: вложенность больше 3 уровней, пустой каталог, двоичный файл
make("deep.zip", [
    ("bin/logo.png", PNG),
    ("etc/motd", "Welcome to the shell emulator!\n"),
    ("home/user/hello.txt", "hello world\nsecond line\n"),
    ("home/user/docs/report.txt", "report v1\n"),
    ("home/user/docs/notes/todo.txt", "1. finish stage 4\n2. finish stage 5\n"),
    ("home/user/docs/notes/archive/old.txt", "old notes\n"),
    ("tmp/", b""),
    ("var/log/app/app.log", "started\nstopped\n"),
])

# Пустой ZIP-архив
make("empty.zip", [])

# Файл, который не является ZIP-архивом
with open(os.path.join(HERE, "broken.zip"), "w") as f:
    f.write("this is not a zip archive\n")
