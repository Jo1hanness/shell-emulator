import javax.swing.*;
import java.awt.*;

/** Главное окно эмулятора. */
public class ShellWindow extends JFrame {
    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private static final Color BG = new Color(30, 30, 30);    // почти чёрный
    private static final Color FG = new Color(0, 220, 120);   // зелёный текст

// в конструкторе, после создания output и input:

    /** Создаёт окно; vfsName показывается в заголовке. */
    public ShellWindow(String vfsName) {
        super("Shell emulator — " + vfsName);
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);
        input.addActionListener(e -> onEnter());   // срабатывает по Enter
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        output.setBackground(BG);
    output.setForeground(FG);
    output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));  // моноширинный шрифт, как в терминале
    input.setBackground(BG);
    input.setForeground(FG);
    input.setCaretColor(FG);   // цвет мигающего курсора, иначе он будет чёрным и пропадёт на тёмном фоне
    input.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    }

    private void onEnter() {
        String line = input.getText();
        input.setText("");
        output.append("$ " + line + "\n");
        // передает line в Parser, а результат — в CommandExecutor
    }
    
}