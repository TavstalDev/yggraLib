package com.github.TavstalDev.yggraLib.impl.vold.items.meta;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import com.github.TavstalDev.yggraLib.impl.vold.utils.ColorUtils_old;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A handler for serializing and deserializing the metadata of a {@link FireworkMeta}.
 * This class provides static methods to convert the firework effects and power of a firework
 * into a storable format and reconstruct them from the stored data.
 */
public class FireworkMetaHandler_old implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkMeta))
            return;

        FireworkMeta fireworkMeta = (FireworkMeta)meta;
        if (fireworkMeta.hasEffects()) {
            List<Map<String, Object>> effects = new ArrayList<>();
            for (FireworkEffect effect : fireworkMeta.getEffects()) {
                Map<String, Object> effectData = new HashMap<>();
                effectData.put("type", effect.getType().name());
                effectData.put("flicker", effect.hasFlicker());
                effectData.put("trail", effect.hasTrail());

                // Colors
                List<Color> colorList = effect.getColors();
                List<String> colors = new ArrayList<>();
                for (Color color : colorList) {
                    colors.add(ColorUtils_old.serializeColor(color));
                }
                effectData.put("colors", colors);

                // FadeColors
                List<Color> fadeColor = effect.getFadeColors();
                List<String> fadeColors = new ArrayList<>();
                for (Color color : fadeColor) {
                    fadeColors.add(ColorUtils_old.serializeColor(color));
                }
                effectData.put("fadeColors", fadeColors);
                effects.add(effectData);
            }
            itemData.put("effects", effects);
        }

        itemData.put("power", fireworkMeta.getPower());
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemStack itemStack, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkMeta))
            return;

        FireworkMeta fireworkMeta = (FireworkMeta)meta;
        if (itemData.containsKey("effects")) {

            List<Map<String, Object>> effects = TypeUtils.castAsListOfMaps(itemData.get("effects"), null);
            if (effects == null) {
                throw new IllegalArgumentException("Invalid effects data format.");
            }

            for (Map<String, Object> effectData : effects) {
                FireworkEffect.Type type = FireworkEffect.Type.valueOf((String) effectData.get("type"));
                boolean flicker = (boolean) effectData.get("flicker");
                boolean trail = (boolean) effectData.get("trail");

                List<Color> colors = new ArrayList<>();
                if (effectData.containsKey("colors")) {
                    List<String> rawColors = TypeUtils.castAsList(effectData.get("colors"), null);
                    if (rawColors != null) {
                        for (String colorData : rawColors) {
                            colors.add(ColorUtils_old.deserializeColor(colorData));
                        }
                    }
                }

                List<Color> fadeColors = new ArrayList<>();
                if (effectData.containsKey("fadeColors")) {
                    // Ensure fadeColors is a list of strings
                    List<String> rawFadeColors = TypeUtils.castAsList(effectData.get("fadeColors"), null);
                    if (rawFadeColors != null) {
                        for (String colorData : rawFadeColors) {
                            fadeColors.add(ColorUtils_old.deserializeColor(colorData));
                        }
                    }
                }

                fireworkMeta.addEffect(FireworkEffect.builder().flicker(flicker).trail(trail).with(type).withColor(colors).withFade(fadeColors).build());
            }
        }

        if (itemData.containsKey("power")) {
            fireworkMeta.setPower((int) itemData.get("power"));
        }
    }
}