package com.github.TavstalDev.yggraLib.impl.vmodern.utils;

import org.bukkit.Color;

/**
 * Modern implementation of ColorUtils for newer Minecraft versions.
 */
public class ColorUtils_modern {
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
        int alpha = color.getAlpha();
        return String.format("%s;%s;%s;%s",
                alpha,
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
        return Color.fromARGB(
                Integer.parseInt(color[0]),
                Integer.parseInt(color[1]),
                Integer.parseInt(color[2]),
                Integer.parseInt(color[3])
        );
    }
}
