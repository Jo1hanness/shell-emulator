import java.util.Arrays;

/** Выполняет команды эмулятора. */
public class CommandExecutor {
    private static final int CD_MAX_ARGS = 1;

    private final Shell shell;

    public CommandExecutor(Shell shell) {
        this.shell = shell;
    }

    /** Выполняет команду и возвращает текст результата; при ошибке бросает ShellException. */
    public String execute(String[] parts) throws ShellException {
        if (parts.length == 0 || parts[0].isEmpty()) {
            return "";
        }
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        switch (command) {
            case "echo":
                return String.join(" ", args);
            case "exit":
                System.exit(0);
                return "";
            case "cd":
                if (args.length > CD_MAX_ARGS) {
                    throw new ShellException("cd: too many arguments");
                }
                return "cd: args=" + Arrays.toString(args);
            case "ls":
                return "ls: args=" + Arrays.toString(args);
            default:
                throw new ShellException("Unknown command: " + command);
        }
    }
}
