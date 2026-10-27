import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Точка входа в приложение.
 * Использование: java Main [--vfs PATH] [--script PATH]
 */
public class Main {
    public static void main(String[] args) {
        String vfsPath = null;
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--vfs":
                case "--script":
                    if (i + 1 >= args.length) {
                        System.err.println("Error: option " + args[i] + " requires a value");
                        System.exit(1);
                    }
                    if (args[i].equals("--vfs")) {
                        vfsPath = args[++i];
                    } else {
                        scriptPath = args[++i];
                    }
                    break;
                default:
                    System.err.println("Error: unknown option " + args[i]);
                    System.err.println("Usage: java Main [--vfs PATH] [--script PATH]");
                    System.exit(1);
            }
        }

        String[] debug = {
            "[debug] vfs path:    " + (vfsPath == null ? "(not set)" : vfsPath),
            "[debug] script path: " + (scriptPath == null ? "(not set)" : scriptPath),
        };
        for (String line : debug) {
            System.out.println(line);
        }

        String vfsName = vfsPath == null ? "no vfs" : Paths.get(vfsPath).getFileName().toString();
        Path script = scriptPath == null ? null : Paths.get(scriptPath);
        Shell shell = new Shell();

        SwingUtilities.invokeLater(() -> {
            ShellWindow window = new ShellWindow(shell, vfsName);
            for (String line : debug) {
                window.println(line);
            }
            window.setVisible(true);
            if (script != null) {
                window.runScript(script);
            }
        });
    }
}
