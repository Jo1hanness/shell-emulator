import java.util.Arrays;
/** Выполняет команды эмулятора. */
public class CommandExecutor {
    private static final int CD_MAX_ARGS = 1;

    public static String execute(String[] parts) {
        if (parts[0].isEmpty()) {
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
                    return "cd: too many arguments";
                }
                return "cd: args=" + Arrays.toString(args);
            case "ls":
                return "ls: args=" + Arrays.toString(args);
            default:
                return "Unknown command: " + command;
        }
    }

}
