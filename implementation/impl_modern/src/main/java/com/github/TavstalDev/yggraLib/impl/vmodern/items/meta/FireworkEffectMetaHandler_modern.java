package com.github.TavstalDev.yggraLib.impl.vmodern.items.meta;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import com.github.TavstalDev.yggraLib.bukkit.items.ItemSerializerBase;
import com.github.TavstalDev.yggraLib.impl.vmodern.utils.ColorUtils_modern;
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
public class FireworkEffectMetaHandler_modern implements IMetaHandler {

    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof FireworkEffectMeta fireworkEffectMeta))
            return;

        if (!fireworkEffectMeta.hasEffect() || fireworkEffectMeta.getEffect() == null)
            return;

        FireworkEffect effect = fireworkEffectMeta.getEffect();
        Map<String, Object> effectData = new HashMap<>();
        effectData.put("type", effect.getType().name());
        effectData.put("flicker", effect.hasFlicker());
        effectData.put("trail", effect.hasTrail());

        // Colors
        List<Color> colorList = effect.getColors();
        List<String> colors = new ArrayList<>();
        for (Color color : colorList) {
            colors.add(ColorUtils_modern.serializeColor(color));
        }
        effectData.put("colors", colors);

        // FadeColors
        List<Color> fadeColor = effect.getFadeColors();
        List<String> fadeColors = new ArrayList<>();
        for (Color color : fadeColor) {
            fadeColors.add(ColorUtils_modern.serializeColor(color));
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
                    colors.add(ColorUtils_modern.deserializeColor(colorData));
                }
            }
        }

        List<Color> fadeColors = new ArrayList<>();
        if (effectData.containsKey("fadeColors")) {
            List<String> rawColors = TypeUtils.castAsList(effectData.get("fadeColors"), null);
            if (rawColors != null) {
                for (String colorData : rawColors) {
                    fadeColors.add(ColorUtils_modern.deserializeColor(colorData));
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