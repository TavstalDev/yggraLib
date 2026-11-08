package io.github.tavstaldev.yggraLib.impl.vmodern.items.meta;

import io.github.tavstaldev.yggraLib.api.utils.TypeUtils;
import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.impl.vmodern.utils.ColorUtils_modern;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
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
public class FireworkMetaHandler_modern implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkMeta fireworkMeta))
            return;

        if (fireworkMeta.hasEffects()) {
            List<Map<String, Object>> effects = new ArrayList<>();
            for (var effect : fireworkMeta.getEffects()) {
                Map<String, Object> effectData = new HashMap<>();
                effectData.put("type", effect.getType().name());
                effectData.put("flicker", effect.hasFlicker());
                effectData.put("trail", effect.hasTrail());

                // Colors
                var colorList = effect.getColors();
                List<String> colors = new ArrayList<>();
                for (Color color : colorList) {
                    colors.add(ColorUtils_modern.serializeColor(color));
                }
                effectData.put("colors", colors);

                // FadeColors
                var fadeColor = effect.getFadeColors();
                List<String> fadeColors = new ArrayList<>();
                for (Color color : fadeColor) {
                    fadeColors.add(ColorUtils_modern.serializeColor(color));
                }
                effectData.put("fadeColors", fadeColors);
                effects.add(effectData);
            }
            itemData.put("effects", effects);
        }

        itemData.put("power", fireworkMeta.getPower());
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkMeta fireworkMeta))
            return;

        if (itemData.containsKey("effects")) {

            List<Map<String, Object>> effects = TypeUtils.castAsListOfMaps(itemData.get("effects"), null);
            if (effects == null) {
                throw new IllegalArgumentException("Invalid effects data format.");
            }

            for (var effectData : effects) {
                FireworkEffect.Type type = FireworkEffect.Type.valueOf((String) effectData.get("type"));
                boolean flicker = (boolean) effectData.get("flicker");
                boolean trail = (boolean) effectData.get("trail");

                List<Color> colors = new ArrayList<>();
                if (effectData.containsKey("colors")) {
                    List<String> rawColors = TypeUtils.castAsList(effectData.get("colors"), null);
                    if (rawColors != null) {
                        for (String colorData : rawColors) {
                            colors.add(ColorUtils_modern.deserializeColor(colorData));
                        }
                    }
                }

                List<Color> fadeColors = new ArrayList<>();
                if (effectData.containsKey("fadeColors")) {
                    // Ensure fadeColors is a list of strings
                    List<String> rawFadeColors = TypeUtils.castAsList(effectData.get("fadeColors"), null);
                    if (rawFadeColors != null) {
                        for (String colorData : rawFadeColors) {
                            fadeColors.add(ColorUtils_modern.deserializeColor(colorData));
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