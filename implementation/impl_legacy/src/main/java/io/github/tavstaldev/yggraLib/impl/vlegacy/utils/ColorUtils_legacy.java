package io.github.tavstaldev.yggraLib.impl.vlegacy.utils;

import org.bukkit.Color;

public class ColorUtils_legacy {
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

    public static Color deserializeColor(String colorData) {
        String[] color = colorData.split(";");
        return Color.fromRGB(
                Integer.parseInt(color[1]),
                Integer.parseInt(color[2]),
                Integer.parseInt(color[3])
        );
    }
}
