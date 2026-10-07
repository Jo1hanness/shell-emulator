import javax.swing.*;
import java.awt.*;

/** Главное окно эмулятора. */
public class ShellWindow extends JFrame {
    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private static final Color BG = new Color(30, 30, 30);
    private static final Color FG = new Color(0, 220, 120);
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    /** Создаёт окно; vfsName показывается в заголовке. */
    public ShellWindow(String vfsName) {
        super("Shell emulator — " + vfsName);
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

    private void onEnter() {
        String line = input.getText();
        input.setText("");
        output.append("$ " + line + "\n");
        String[] parts = Parser.parse(line);
        String result = CommandExecutor.execute(parts);
        if (!result.isEmpty()) {
            output.append(result + "\n");
        }
    }
}