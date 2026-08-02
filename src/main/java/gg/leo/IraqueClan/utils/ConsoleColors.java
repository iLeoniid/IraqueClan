package gg.leo.IraqueClan.utils;

public final class ConsoleColors {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String STRIKETHROUGH = "\u001B[9m";

    public static final String BLACK = "\u001B[30m";
    public static final String DARK_RED = "\u001B[31m";
    public static final String DARK_GREEN = "\u001B[32m";
    public static final String DARK_YELLOW = "\u001B[33m";
    public static final String DARK_BLUE = "\u001B[34m";
    public static final String DARK_MAGENTA = "\u001B[35m";
    public static final String DARK_CYAN = "\u001B[36m";
    public static final String DARK_GRAY = "\u001B[90m";
    public static final String GRAY = "\u001B[90m";
    public static final String RED = "\u001B[91m";
    public static final String GREEN = "\u001B[92m";
    public static final String YELLOW = "\u001B[93m";
    public static final String BLUE = "\u001B[94m";
    public static final String MAGENTA = "\u001B[95m";
    public static final String CYAN = "\u001B[96m";
    public static final String WHITE = "\u001B[97m";

    private ConsoleColors() {}

    public static String translate(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if ((c == '&' || c == '\u00a7') && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                if (next == '#') {
                    if (i + 8 < s.length() && isHex(s.charAt(i + 2)) && isHex(s.charAt(i + 3))
                            && isHex(s.charAt(i + 4)) && isHex(s.charAt(i + 5))
                            && isHex(s.charAt(i + 6)) && isHex(s.charAt(i + 7))) {
                        sb.append(hexToAnsi(s.substring(i + 2, i + 8)));
                        i += 8;
                        continue;
                    }
                    sb.append(c);
                    i++;
                    continue;
                }
                String ansi = codeToAnsi(Character.toLowerCase(next));
                if (ansi != null) {
                    sb.append(ansi);
                    i += 2;
                    continue;
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    private static boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static String hexToAnsi(String hex) {
        try {
            int rgb = Integer.parseInt(hex, 16);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            int max = Math.max(r, Math.max(g, b));
            int diff = Math.abs(r - g) + Math.abs(g - b) + Math.abs(r - b);
            if (diff < 80) {
                int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                if (lum < 80) return BLACK;
                if (lum < 180) return GRAY;
                return WHITE;
            }
            if (max == r && r > g + 40 && r > b + 40) return r > 160 ? RED : DARK_RED;
            if (max == g && g > r + 40 && g > b + 40) return g > 160 ? GREEN : DARK_GREEN;
            if (max == b && b > r + 40 && b > g + 40) return b > 160 ? BLUE : DARK_BLUE;
            if (r > 160 && g > 160 && b < 100) return YELLOW;
            if (r > 160 && b > 160 && g < 100) return MAGENTA;
            if (g > 140 && b > 140 && r < 100) return CYAN;
            return WHITE;
        } catch (Exception e) {
            return "";
        }
    }

    private static String codeToAnsi(char c) {
        return switch (c) {
            case '0' -> BLACK;
            case '1' -> DARK_BLUE;
            case '2' -> DARK_GREEN;
            case '3' -> DARK_CYAN;
            case '4' -> DARK_RED;
            case '5' -> DARK_MAGENTA;
            case '6' -> DARK_YELLOW;
            case '7' -> GRAY;
            case '8' -> GRAY;
            case '9' -> BLUE;
            case 'a' -> GREEN;
            case 'b' -> CYAN;
            case 'c' -> RED;
            case 'd' -> MAGENTA;
            case 'e' -> YELLOW;
            case 'f' -> WHITE;
            case 'k' -> "";
            case 'l' -> BOLD;
            case 'm' -> STRIKETHROUGH;
            case 'n' -> UNDERLINE;
            case 'o' -> ITALIC;
            case 'r' -> RESET;
            default -> null;
        };
    }
}
