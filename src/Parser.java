import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Разбор строки команды. */
public class Parser {
    /** Раскрывает переменные окружения вида $NAME в строке. */
    public static String expandVariables(String text) {
        Pattern p = Pattern.compile("\\$(\\w+)");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String name = m.group(1);
            String value = System.getenv(name);
            if (value == null) {
                value = "";
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(value));
        }

        m.appendTail(sb);
        return sb.toString();
    }

    /** Раскрывает переменные и разбивает строку на слова. */
    public static String[] parse(String line) {
        String expanded = expandVariables(line);
        String[] parts = expanded.trim().split("\\s+");
        return parts;
    }
}
