package io.github.tavstaldev.yggra.impl.v1_21_5.utils;

import org.bukkit.Color;

public class ColorUtils_1_21_5 {
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
