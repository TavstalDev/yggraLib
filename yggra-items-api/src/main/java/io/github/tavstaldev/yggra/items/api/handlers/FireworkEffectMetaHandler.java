package io.github.tavstaldev.yggra.items.api.handlers;

import io.github.tavstaldev.yggra.items.api.IMetaHandler;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import io.github.tavstaldev.yggra.items.api.utils.ColorUtils;
import io.github.tavstaldev.yggra.items.api.utils.TypeUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A handler for serializing and deserializing the firework effect metadata of a {@link FireworkEffectMeta}.
 * This class provides static methods to convert the firework effect of an item into a storable format
 * and reconstruct it from the stored data.
 */
public class FireworkEffectMetaHandler implements IMetaHandler {

    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkEffectMeta fireworkEffectMeta))
            return;

        if (!fireworkEffectMeta.hasEffect() || fireworkEffectMeta.getEffect() == null)
            return;

        var effect = fireworkEffectMeta.getEffect();
        Map<String, Object> effectData = new HashMap<>();
        effectData.put("type", effect.getType().name());
        effectData.put("flicker", effect.hasFlicker());
        effectData.put("trail", effect.hasTrail());

        // Colors
        var colorList = effect.getColors();
        List<String> colors = new ArrayList<>();
        for (Color color : colorList) {
            colors.add(ColorUtils.serializeColor(color));
        }
        effectData.put("colors", colors);

        // FadeColors
        var fadeColor = effect.getFadeColors();
        List<String> fadeColors = new ArrayList<>();
        for (Color color : fadeColor) {
            fadeColors.add(ColorUtils.serializeColor(color));
        }
        effectData.put("fadeColors", fadeColors);

        itemData.put("effect", effectData);
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkEffectMeta fireworkEffectMeta))
            return;

        if (!itemData.containsKey("effect"))
            return;

        Map<String, Object> effectData = TypeUtils.castAsMap(itemData.get("effect"), null);
        if (effectData == null || effectData.isEmpty())
            throw new Exception("Invalid effect data for FireworkEffectMeta");

        FireworkEffect.Type type = FireworkEffect.Type.valueOf((String) effectData.get("type"));
        boolean flicker = (boolean) effectData.get("flicker");
        boolean trail = (boolean) effectData.get("trail");

        List<Color> colors = new ArrayList<>();
        if (effectData.containsKey("colors")) {
            List<String> rawColors = TypeUtils.castAsList(effectData.get("colors"), null);
            if (rawColors != null) {
                for (String colorData : rawColors) {
                    colors.add(ColorUtils.deserializeColor(colorData));
                }
            }
        }

        List<Color> fadeColors = new ArrayList<>();
        if (effectData.containsKey("fadeColors")) {
            List<String> rawColors = TypeUtils.castAsList(effectData.get("fadeColors"), null);
            if (rawColors != null) {
                for (String colorData : rawColors) {
                    fadeColors.add(ColorUtils.deserializeColor(colorData));
                }
            }
        }

        fireworkEffectMeta.setEffect(
                FireworkEffect.builder()
                        .flicker(flicker)
                        .trail(trail)
                        .with(type)
                        .withColor(colors)
                        .withFade(fadeColors)
                        .build()
        );
    }
}