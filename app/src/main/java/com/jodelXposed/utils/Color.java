package com.jodelXposed.utils;

public class Color {
    public static boolean compareColor(String color, String color2) {
        return normalizeColor(color).equalsIgnoreCase(normalizeColor(color2));
    }

    public static String format6Hash(String color) {
        return normalizeColor(color);
    }

    public static String format9Hash(String color) {
        color = normalizeColor(color);
        return color.substring(0, 1) + "FF" + color.substring(3, 6);
    }

    public static String format6(String color) {
        color = normalizeColor(color);
        return color.substring(1, 7);
    }

    public static String format9(String color) {
        color = normalizeColor(color);
        return "FF" + color.substring(1, 7);
    }

    /**
     * Normalize color to #123456 format
     * This removes transparency
     *
     * @param color a color in format 123456, #123456, #FF123456
     * @return normalized color
     */
    public static String normalizeColor(String color) {
        if (!color.startsWith("#")) {
            color = "#" + color;
        }
        if (color.length() == 9) {
            color = color.substring(0, 1) + color.substring(3, 9);
        }
        return color;
    }
}
