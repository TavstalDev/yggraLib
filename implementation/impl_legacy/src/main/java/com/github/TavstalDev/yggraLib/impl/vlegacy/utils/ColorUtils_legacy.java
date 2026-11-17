package com.github.TavstalDev.yggraLib.impl.vlegacy.utils;

import org.bukkit.Color;

/**
 * Legacy implementation of ColorUtils for older Minecraft versions.
 */
public class ColorUtils_legacy {
    /**
     * Converts a Bukkit color to a string.
     * 
     * @param color the color to convert
     * @return the serialized color string
     */
    public static String serializeColor(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("%s;%s;%s;%s",
                255,
                red,
                green,
                blue
        );
    }

    /**
     * Converts a serialized color string to a Bukkit color.
     * 
     * @param colorData the serialized color string
     * @return the deserialized color
     */
    public static Color deserializeColor(String colorData) {
        String[] color = colorData.split(";");
        return Color.fromRGB(
                Integer.parseInt(color[1]),
                Integer.parseInt(color[2]),
                Integer.parseInt(color[3])
        );
    }
}
