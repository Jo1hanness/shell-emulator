/** Ошибка выполнения команды эмулятора (неизвестная команда, неверные аргументы и т.п.). */
public class ShellException extends Exception {
    public ShellException(String message) {
        super(message);
    }
}
