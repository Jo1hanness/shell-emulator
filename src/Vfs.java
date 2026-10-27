import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Виртуальная файловая система. Источник — ZIP-архив, который читается целиком в память;
 * архив не распаковывается на диск и не изменяется. Содержимое файлов хранится в base64.
 */
public class Vfs {
    private final String name;
    private final VfsNode root = VfsNode.directory("");

    private Vfs(String name) {
        this.name = name;
    }

    /** Пустая VFS (когда параметр --vfs не задан). */
    public static Vfs empty() {
        return new Vfs("no vfs");
    }

    /** Загружает VFS из ZIP-архива. */
    public static Vfs load(Path zipPath) throws ShellException {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(zipPath);
        } catch (NoSuchFileException e) {
            throw new ShellException("VFS error: file not found: " + zipPath);
        } catch (IOException e) {
            throw new ShellException("VFS error: cannot read " + zipPath + ": " + e.getMessage());
        }
        if (!isZip(bytes)) {
            throw new ShellException("VFS error: not a ZIP archive: " + zipPath);
        }

        Vfs vfs = new Vfs(zipPath.getFileName().toString());
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entry.isDirectory()) {
                    vfs.makeDirs(entryName);
                } else {
                    vfs.addFile(entryName, readAll(zip));
                }
            }
        } catch (IOException e) {
            throw new ShellException("VFS error: broken ZIP archive: " + e.getMessage());
        }
        return vfs;
    }

    public String getName() { return name; }
    public VfsNode getRoot() { return root; }

    /** Количество файлов и каталогов (без корня): {dirs, files}. */
    public int[] count() {
        int[] result = new int[2];
        countRec(root, result);
        return result;
    }

    private static void countRec(VfsNode node, int[] acc) {
        for (VfsNode child : node.getChildren().values()) {
            if (child.isDirectory()) {
                acc[0]++;
                countRec(child, acc);
            } else {
                acc[1]++;
            }
        }
    }

    private static boolean isZip(byte[] b) {
        // Сигнатуры ZIP: PK\3\4 — обычный архив, PK\5\6 — пустой архив
        return b.length >= 4 && b[0] == 'P' && b[1] == 'K'
                && ((b[2] == 3 && b[3] == 4) || (b[2] == 5 && b[3] == 6));
    }

    private static byte[] readAll(ZipInputStream zip) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n;
        while ((n = zip.read(chunk)) > 0) {
            buf.write(chunk, 0, n);
        }
        return buf.toByteArray();
    }

    /** Создаёт (при необходимости) цепочку каталогов и возвращает последний. */
    private VfsNode makeDirs(String path) throws IOException {
        VfsNode current = root;
        for (String part : path.split("/")) {
            if (part.isEmpty()) {
                continue;
            }
            VfsNode next = current.getChild(part);
            if (next == null) {
                next = VfsNode.directory(part);
                current.addChild(next);
            } else if (!next.isDirectory()) {
                throw new IOException("path conflict: " + path);
            }
            current = next;
        }
        return current;
    }

    private void addFile(String path, byte[] data) throws IOException {
        int slash = path.lastIndexOf('/');
        VfsNode dir = slash < 0 ? root : makeDirs(path.substring(0, slash));
        dir.addChild(VfsNode.file(path.substring(slash + 1), data));
    }
}
