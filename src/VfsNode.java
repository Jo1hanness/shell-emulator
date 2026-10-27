import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/** Узел виртуальной файловой системы: файл или каталог. Хранится только в памяти. */
public class VfsNode {
    private String name;
    private final boolean directory;
    private VfsNode parent;
    /** Содержимое файла в base64 (для каталогов — null). */
    private String base64Data;
    /** Дочерние узлы, отсортированные по имени (для файлов — null). */
    private final Map<String, VfsNode> children;

    private VfsNode(String name, boolean directory) {
        this.name = name;
        this.directory = directory;
        this.children = directory ? new TreeMap<>() : null;
    }

    public static VfsNode directory(String name) {
        return new VfsNode(name, true);
    }

    public static VfsNode file(String name, byte[] data) {
        VfsNode node = new VfsNode(name, false);
        node.setData(data);
        return node;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isDirectory() { return directory; }
    public VfsNode getParent() { return parent; }
    public Map<String, VfsNode> getChildren() { return children; }

    public VfsNode getChild(String childName) {
        return directory ? children.get(childName) : null;
    }

    /** Добавляет дочерний узел (заменяя одноимённый). */
    public void addChild(VfsNode child) {
        child.parent = this;
        children.put(child.name, child);
    }

    public void removeChild(String childName) {
        VfsNode removed = children.remove(childName);
        if (removed != null) {
            removed.parent = null;
        }
    }

    public String getBase64Data() { return base64Data; }

    /** Возвращает исходные (декодированные из base64) байты файла. */
    public byte[] getData() {
        return Base64.getDecoder().decode(base64Data);
    }

    public void setData(byte[] data) {
        this.base64Data = Base64.getEncoder().encodeToString(data);
    }

    /** Размер файла в байтах. */
    public int size() {
        return directory ? 0 : getData().length;
    }

    /** Абсолютный путь узла, например /home/user. */
    public String path() {
        if (parent == null) {
            return "/";
        }
        String parentPath = parent.path();
        return (parentPath.equals("/") ? "" : parentPath) + "/" + name;
    }
}
