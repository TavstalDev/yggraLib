package io.github.tavstaldev.yggraLib.impl.v1_21_5.items.meta;

import io.github.tavstaldev.yggraLib.api.utils.TypeUtils;
import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
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
public class PotionMetaHandler_1_21_5 implements IMetaHandler {
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

        // Custom Effects
        if (itemData.containsKey("customEffects")) {
            Map<String, Object> effects = TypeUtils.castAsMap(itemData.get("customEffects"), null);
            if (effects == null)
                return;

            for (Map.Entry<String, Object> entry : effects.entrySet()) {
                Map<String, Object> data = TypeUtils.castAsMap(entry.getValue(), null);
                if (data == null)
                    continue;

                PotionEffectType type = parent.registryAccessor.getPotionEffectType(entry.getKey());
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