package np.edu.kathford.www;

@SuppressWarnings("ALL")
public class FormattedLogger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_UNDERLINED = "\u001B[4m";
    public static final String ANSI_BLINK = "\u001B[5m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void printHeading1(String heading) {
        System.out.println(ANSI_BLINK+ANSI_UNDERLINED + ANSI_BOLD + ANSI_YELLOW + heading + ANSI_RESET);
    }

    public static void printHeading2(String heading) {
        System.out.println(ANSI_YELLOW + heading + ANSI_RESET);
    }

    public static void printKVFormatted(String key, String value) {
        System.out.println(ANSI_BLUE + key + ANSI_RESET + " = " + ANSI_GREEN + "\"" + value + "\"" + ANSI_RESET);
    }

    public static void printProcessStatus(String status) {
        System.out.println(ANSI_PURPLE + status + ANSI_RESET);
    }
}
