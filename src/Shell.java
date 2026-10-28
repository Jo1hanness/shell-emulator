/** Ядро эмулятора: хранит состояние сессии и выполняет строки команд. Не зависит от GUI. */
public class Shell {
    private final Vfs vfs;
    private final long startTime = System.currentTimeMillis();
    private VfsNode cwd;
    private final CommandExecutor executor = new CommandExecutor(this);

    public Shell(Vfs vfs) {
        this.vfs = vfs;
        this.cwd = vfs.getRoot();
    }

    public Vfs getVfs() { return vfs; }
    public VfsNode getCwd() { return cwd; }
    public void setCwd(VfsNode cwd) { this.cwd = cwd; }
    public long getStartTime() { return startTime; }

    /**
     * Находит узел VFS по пути. Поддерживаются абсолютные и относительные пути, "." и "..".
     * Возвращает null, если узел не найден.
     */
    public VfsNode resolve(String path) {
        VfsNode node = path.startsWith("/") ? vfs.getRoot() : cwd;
        for (String part : path.split("/")) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            if (part.equals("..")) {
                if (node.getParent() != null) {
                    node = node.getParent();
                }
                continue;
            }
            node = node.getChild(part);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /** Разбирает строку и выполняет команду. Возвращает текст вывода. */
    public String run(String line) throws ShellException {
        String[] parts = Parser.parse(line);
        return executor.execute(parts);
    }

    /** Приглашение командной строки: текущий каталог VFS. */
    public String prompt() {
        return cwd.path() + " $ ";
    }
}
