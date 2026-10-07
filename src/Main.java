import javax.swing.SwingUtilities;

/** Точка входа в приложение. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShellWindow("myvfs").setVisible(true));
    }
}
