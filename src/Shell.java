/** Ядро эмулятора: хранит состояние сессии и выполняет строки команд. Не зависит от GUI. */
public class Shell {
    private final Vfs vfs;
    private VfsNode cwd;
    private final CommandExecutor executor = new CommandExecutor(this);

    public Shell(Vfs vfs) {
        this.vfs = vfs;
        this.cwd = vfs.getRoot();
    }

    public Vfs getVfs() { return vfs; }
    public VfsNode getCwd() { return cwd; }
    public void setCwd(VfsNode cwd) { this.cwd = cwd; }

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
