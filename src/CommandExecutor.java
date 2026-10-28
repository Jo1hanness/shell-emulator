import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Выполняет команды эмулятора. */
public class CommandExecutor {
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
            case "ls":
                return ls(args);
            case "cd":
                return cd(args);
            case "uptime":
                return uptime(args);
            case "cal":
                return cal(args);
            case "rev":
                return rev(args);
            case "rm":
                return rm(args);
            case "cp":
                return cp(args);
            default:
                throw new ShellException("Unknown command: " + command);
        }
    }

    // ---------- ls ----------

    /** ls [-l] [PATH...] — содержимое каталогов VFS. */
    private String ls(String[] args) throws ShellException {
        boolean longFormat = false;
        List<String> paths = new ArrayList<>();
        for (String arg : args) {
            if (arg.equals("-l")) {
                longFormat = true;
            } else if (arg.startsWith("-")) {
                throw new ShellException("ls: invalid option '" + arg + "'");
            } else {
                paths.add(arg);
            }
        }
        if (paths.isEmpty()) {
            paths.add(".");
        }

        StringBuilder out = new StringBuilder();
        for (String path : paths) {
            VfsNode node = shell.resolve(path);
            if (node == null) {
                throw new ShellException("ls: cannot access '" + path + "': No such file or directory");
            }
            if (paths.size() > 1 && node.isDirectory()) {
                out.append(path).append(":\n");
            }
            if (node.isDirectory()) {
                for (VfsNode child : node.getChildren().values()) {
                    out.append(formatEntry(child, longFormat)).append('\n');
                }
            } else {
                out.append(formatEntry(node, longFormat)).append('\n');
            }
            if (paths.size() > 1) {
                out.append('\n');
            }
        }
        return out.toString().trim();
    }

    private static String formatEntry(VfsNode node, boolean longFormat) {
        String name = node.isDirectory() ? node.getName() + "/" : node.getName();
        if (!longFormat) {
            return name;
        }
        String type = node.isDirectory() ? "d" : "-";
        return String.format("%s %6d  %s", type, node.size(), name);
    }

    // ---------- cd ----------

    /** cd [PATH] — смена текущего каталога; без аргументов — переход в корень. */
    private String cd(String[] args) throws ShellException {
        if (args.length > 1) {
            throw new ShellException("cd: too many arguments");
        }
        String path = args.length == 0 ? "/" : args[0];
        VfsNode node = shell.resolve(path);
        if (node == null) {
            throw new ShellException("cd: " + path + ": No such file or directory");
        }
        if (!node.isDirectory()) {
            throw new ShellException("cd: " + path + ": Not a directory");
        }
        shell.setCwd(node);
        return "";
    }

    // ---------- uptime ----------

    /** uptime — текущее время и время работы эмулятора. */
    private String uptime(String[] args) throws ShellException {
        if (args.length > 0) {
            throw new ShellException("uptime: extra operand '" + args[0] + "'");
        }
        long seconds = (System.currentTimeMillis() - shell.getStartTime()) / 1000;
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return String.format("%s up %d:%02d:%02d, 1 user",
                now, seconds / 3600, seconds / 60 % 60, seconds % 60);
    }

    // ---------- cal ----------

    /** cal — текущий месяц; cal YEAR — весь год; cal MONTH YEAR — указанный месяц. */
    private String cal(String[] args) throws ShellException {
        LocalDate today = LocalDate.now();
        switch (args.length) {
            case 0:
                return month(YearMonth.from(today));
            case 1: {
                int year = parseNumber(args[0], 1, 9999, "year");
                StringBuilder out = new StringBuilder();
                for (int m = 1; m <= 12; m++) {
                    out.append(month(YearMonth.of(year, m))).append("\n\n");
                }
                return out.toString().trim();
            }
            case 2: {
                int month = parseNumber(args[0], 1, 12, "month");
                int year = parseNumber(args[1], 1, 9999, "year");
                return month(YearMonth.of(year, month));
            }
            default:
                throw new ShellException("cal: too many arguments");
        }
    }

    private static int parseNumber(String text, int min, int max, String what) throws ShellException {
        try {
            int value = Integer.parseInt(text);
            if (value < min || value > max) {
                throw new ShellException("cal: " + what + " '" + text + "' not in range " + min + ".." + max);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ShellException("cal: invalid " + what + " '" + text + "'");
        }
    }

    /** Календарь одного месяца в формате UNIX-утилиты cal. */
    private static String month(YearMonth ym) {
        StringBuilder out = new StringBuilder();
        String title = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + ym.getYear();
        int pad = (20 - title.length()) / 2;
        out.append(" ".repeat(Math.max(0, pad))).append(title).append('\n');
        out.append("Su Mo Tu We Th Fr Sa\n");

        // Воскресенье — первый день недели (как в cal)
        int offset = ym.atDay(1).getDayOfWeek().getValue() % 7;
        StringBuilder line = new StringBuilder("   ".repeat(offset));
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            line.append(String.format("%2d ", day));
            if (ym.atDay(day).getDayOfWeek() == DayOfWeek.SATURDAY) {
                out.append(stripEnd(line)).append('\n');
                line.setLength(0);
            }
        }
        if (line.length() > 0) {
            out.append(stripEnd(line)).append('\n');
        }
        return stripEnd(out);
    }

    private static String stripEnd(CharSequence s) {
        return s.toString().replaceAll("\\s+$", "");
    }

    // ---------- rev ----------

    /** rev FILE... — выводит строки файлов VFS, перевернув каждую посимвольно. */
    private String rev(String[] args) throws ShellException {
        if (args.length == 0) {
            throw new ShellException("rev: missing file operand");
        }
        StringBuilder out = new StringBuilder();
        for (String path : args) {
            VfsNode node = shell.resolve(path);
            if (node == null) {
                throw new ShellException("rev: " + path + ": No such file or directory");
            }
            if (node.isDirectory()) {
                throw new ShellException("rev: " + path + ": Is a directory");
            }
            String text = new String(node.getData(), StandardCharsets.UTF_8);
            if (text.endsWith("\n")) {
                text = text.substring(0, text.length() - 1);
            }
            for (String line : text.split("\n", -1)) {
                out.append(new StringBuilder(line).reverse()).append('\n');
            }
        }
        return stripEnd(out);
    }

    // ---------- общие для rm/cp ----------

    /** Отделяет опцию -r (или -R) от остальных аргументов. */
    private static boolean parseRecursive(String cmd, String[] args, List<String> operands) throws ShellException {
        boolean recursive = false;
        for (String arg : args) {
            if (arg.equals("-r") || arg.equals("-R")) {
                recursive = true;
            } else if (arg.startsWith("-") && arg.length() > 1) {
                throw new ShellException(cmd + ": invalid option '" + arg + "'");
            } else {
                operands.add(arg);
            }
        }
        return recursive;
    }

    /** Убирает завершающие '/' (кроме корня). */
    private static String trimSlashes(String path) {
        String p = path;
        while (p.length() > 1 && p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    // ---------- rm ----------

    /** rm [-r] PATH... — удаляет файлы (с -r — и каталоги) из VFS. Изменения только в памяти. */
    private String rm(String[] args) throws ShellException {
        List<String> paths = new ArrayList<>();
        boolean recursive = parseRecursive("rm", args, paths);
        if (paths.isEmpty()) {
            throw new ShellException("rm: missing operand");
        }
        for (String path : paths) {
            VfsNode node = shell.resolve(path);
            if (node == null) {
                throw new ShellException("rm: cannot remove '" + path + "': No such file or directory");
            }
            if (node.getParent() == null) {
                throw new ShellException("rm: refusing to remove root directory '/'");
            }
            if (node.isDirectory() && !recursive) {
                throw new ShellException("rm: cannot remove '" + path + "': Is a directory");
            }
            if (node.isAncestorOf(shell.getCwd())) {
                throw new ShellException("rm: cannot remove '" + path + "': current directory is inside it");
            }
            node.getParent().removeChild(node.getName());
        }
        return "";
    }

    // ---------- cp ----------

    /**
     * cp [-r] SRC DST — копирует файл (с -r — каталог) внутри VFS.
     * cp [-r] SRC... DIR — копирует несколько источников в существующий каталог.
     * Изменения только в памяти.
     */
    private String cp(String[] args) throws ShellException {
        List<String> operands = new ArrayList<>();
        boolean recursive = parseRecursive("cp", args, operands);
        if (operands.isEmpty()) {
            throw new ShellException("cp: missing file operand");
        }
        if (operands.size() == 1) {
            throw new ShellException("cp: missing destination file operand after '" + operands.get(0) + "'");
        }
        String dstPath = trimSlashes(operands.get(operands.size() - 1));
        List<String> sources = operands.subList(0, operands.size() - 1);
        VfsNode dst = shell.resolve(dstPath);
        if (sources.size() > 1 && (dst == null || !dst.isDirectory())) {
            throw new ShellException("cp: target '" + dstPath + "' is not a directory");
        }

        for (String srcPath : sources) {
            VfsNode src = shell.resolve(trimSlashes(srcPath));
            if (src == null) {
                throw new ShellException("cp: cannot stat '" + srcPath + "': No such file or directory");
            }
            if (src.isDirectory() && !recursive) {
                throw new ShellException("cp: -r not specified; omitting directory '" + srcPath + "'");
            }

            VfsNode targetDir;
            String targetName;
            if (dst != null && dst.isDirectory()) {
                targetDir = dst;
                targetName = src.getParent() == null ? "root" : src.getName();
            } else {
                int slash = dstPath.lastIndexOf('/');
                String parentPath = slash < 0 ? "." : (slash == 0 ? "/" : dstPath.substring(0, slash));
                targetDir = shell.resolve(parentPath);
                targetName = dstPath.substring(slash + 1);
                if (targetDir == null || !targetDir.isDirectory()) {
                    throw new ShellException("cp: cannot create '" + dstPath + "': No such file or directory");
                }
            }

            VfsNode existing = targetDir.getChild(targetName);
            if (existing == src) {
                throw new ShellException("cp: '" + srcPath + "' and '" + dstPath + "' are the same file");
            }
            if (src.isDirectory() && src.isAncestorOf(targetDir)) {
                throw new ShellException("cp: cannot copy a directory '" + srcPath + "' into itself");
            }
            if (existing != null && existing.isDirectory() != src.isDirectory()) {
                throw new ShellException("cp: cannot overwrite " + (existing.isDirectory() ? "directory" : "non-directory")
                        + " '" + existing.path() + "' with " + (src.isDirectory() ? "directory" : "non-directory"));
            }
            if (existing != null && existing.isAncestorOf(shell.getCwd())) {
                throw new ShellException("cp: cannot overwrite '" + existing.path() + "': current directory is inside it");
            }
            targetDir.addChild(src.deepCopy(targetName));
        }
        return "";
    }
}
