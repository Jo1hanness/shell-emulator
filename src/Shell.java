/** Ядро эмулятора: хранит состояние сессии и выполняет строки команд. Не зависит от GUI. */
public class Shell {
    private final CommandExecutor executor = new CommandExecutor(this);

    /** Разбирает строку и выполняет команду. Возвращает текст вывода. */
    public String run(String line) throws ShellException {
        String[] parts = Parser.parse(line);
        return executor.execute(parts);
    }

    /** Приглашение командной строки. */
    public String prompt() {
        return "$ ";
    }
}
