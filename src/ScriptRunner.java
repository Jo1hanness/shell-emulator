import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Выполняет стартовый скрипт построчно, имитируя диалог с пользователем:
 * на экран выводится и введённая команда, и её результат.
 * Строки, начинающиеся с '#', и пустые строки считаются комментариями и пропускаются.
 * При первой ошибке выполнение скрипта останавливается.
 */
public class ScriptRunner {

    /** Выполняет скрипт. Возвращает true, если скрипт выполнен без ошибок. */
    public static boolean run(Shell shell, Path script, Consumer<String> out) {
        List<String> lines;
        try {
            lines = Files.readAllLines(script, StandardCharsets.UTF_8);
        } catch (IOException e) {
            out.accept("Error: cannot read script: " + script);
            return false;
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            out.accept(shell.prompt() + line);
            try {
                String result = shell.run(line);
                if (!result.isEmpty()) {
                    out.accept(result);
                }
            } catch (ShellException e) {
                out.accept(e.getMessage());
                out.accept("Script stopped: error at line " + (i + 1));
                return false;
            }
        }
        return true;
    }
}
