package io.github.tavstaldev.yggra.items.api.handlers;

import io.github.tavstaldev.yggra.items.api.IMetaHandler;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import io.github.tavstaldev.yggra.items.api.utils.ColorUtils;
import io.github.tavstaldev.yggra.items.api.utils.TypeUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * A handler for serializing and deserializing the metadata of a {@link PotionMeta}.
 * This class provides static methods to convert the color, base potion type, and custom effects
 * of a potion into a storable format and reconstruct them from the stored data.
 */
public class PotionMetaHandler implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof PotionMeta potionMeta))
            return;

        // Custom Potion Name
        if (parent.potionNameHandler.hasCustomName(potionMeta)) {
            String customPotionName = parent.potionNameHandler.getCustomName(potionMeta);
            if (customPotionName != null && !customPotionName.isEmpty()) {
                itemData.put("customPotionName", customPotionName);
            }
        }

        // Color
        if (potionMeta.hasColor() && potionMeta.getColor() != null) {
            itemData.put("color", ColorUtils.serializeColor(potionMeta.getColor()));
        }

        // Base Potion Type
        //noinspection ConstantValue
        if (potionMeta.getBasePotionType() != null) {
            itemData.put("basePotionType", potionMeta.getBasePotionType().getKey().getKey());
        }

        // Custom Effects
        if (!potionMeta.getCustomEffects().isEmpty()) {
            Map<String, Object> effects = new HashMap<>();
            for (var customEffect : potionMeta.getCustomEffects()) {
                effects.put(customEffect.getType().getKey().getKey(), customEffect.serialize());
            }
            itemData.put("customEffects", effects);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof PotionMeta potionMeta))
            return;

        // Custom Potion Name
        if (itemData.containsKey("customPotionName")) {
            String customPotionName = (String)itemData.get("customPotionName");
            parent.potionNameHandler.setCustomName(potionMeta, customPotionName);
        }

        // Color
        if (itemData.containsKey("color")) {
            String rawColor = (String) itemData.get("color");
            potionMeta.setColor(ColorUtils.deserializeColor(rawColor));
        }

        // Base Potion Type
        if (itemData.containsKey("basePotionType")) {
            String potion = (String) itemData.get("basePotionType");

            NamespacedKey namespacedKey;
            if (!potion.contains(":"))
                namespacedKey = NamespacedKey.minecraft(potion.toLowerCase());
            else
                namespacedKey = NamespacedKey.fromString(potion);

            if (namespacedKey != null) {
                potionMeta.setBasePotionType(parent.registryAccessor.getPotionType(namespacedKey));
            }
        }

        // Custom Effects
        if (itemData.containsKey("customEffects")) {
            Map<String, Object> effects = TypeUtils.castAsMap(itemData.get("customEffects"), null);
            if (effects == null)
                return;

            for (var entry : effects.entrySet()) {
                NamespacedKey effectKey;
                if (!entry.getKey().contains(":"))
                    effectKey = NamespacedKey.minecraft(entry.getKey().toLowerCase());
                else
                    effectKey = NamespacedKey.fromString(entry.getKey());
                if (effectKey == null)
                    continue;

                Map<String, Object> data = TypeUtils.castAsMap(entry.getValue(), null);
                if (data == null)
                    continue;

                PotionEffectType type = parent.registryAccessor.getPotionEffectType(effectKey);
                if (type == null)
                    continue;

                int duration = (int) data.getOrDefault("duration", 200);
                int amplifier = (int) data.getOrDefault("amplifier", 0);
                boolean ambient = (boolean) data.getOrDefault("ambient", false);
                boolean particles = (boolean) data.getOrDefault("particles", true);
                potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles), true);
            }
        }
    }
}