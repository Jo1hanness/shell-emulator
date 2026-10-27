import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

/** Главное окно эмулятора. */
public class ShellWindow extends JFrame {
    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final Shell shell;
    private static final Color BG = new Color(30, 30, 30);
    private static final Color FG = new Color(0, 220, 120);
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    /** Создаёт окно; vfsName показывается в заголовке. */
    public ShellWindow(Shell shell, String vfsName) {
        super("Shell emulator — " + vfsName);
        this.shell = shell;
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);
        input.addActionListener(e -> onEnter());
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        output.setBackground(BG);
        output.setForeground(FG);
        output.setFont(FONT);
        input.setBackground(BG);
        input.setForeground(FG);
        input.setCaretColor(FG);
        input.setFont(FONT);
    }

    /** Добавляет строку в область вывода. */
    public void println(String text) {
        output.append(text + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }

    /** Выполняет стартовый скрипт, выводя команды и результаты в окно. */
    public void runScript(Path script) {
        println("# running script: " + script);
        boolean ok = ScriptRunner.run(shell, script, this::println);
        println(ok ? "# script finished" : "# script aborted");
    }

    private void onEnter() {
        String line = input.getText();
        input.setText("");
        println(shell.prompt() + line);
        try {
            String result = shell.run(line);
            if (!result.isEmpty()) {
                println(result);
            }
        } catch (ShellException e) {
            println(e.getMessage());
        }
    }
}
