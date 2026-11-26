package com.github.TavstalDev.yggraLib.impl.vlegacy.items.meta;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

/**
 * A handler for serializing and deserializing the metadata of a {@link PotionMeta}.
 * This class provides static methods to convert the color, base potion type, and custom effects
 * of a potion into a storable format and reconstruct them from the stored data.
 */
public class PotionMetaHandler_legacy implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof PotionMeta))
            return;

        PotionMeta potionMeta = (PotionMeta)meta;

        // Base Type
        try {
            PotionData potionData = potionMeta.getBasePotionData();
            if (potionData != null) {
               String name = potionData.getType().name();
               if (potionData.isUpgraded())
                   name = "STRONG_" + name;
               else if (potionData.isExtended())
                   name = "LONG_" + name;

            }
        } catch (Exception ignored) {}

        // Custom Potion Name
        if (parent.potionNameHandler().hasCustomName(potionMeta)) {
            String customPotionName = parent.potionNameHandler().getCustomName(potionMeta);
            if (customPotionName != null && !customPotionName.isEmpty()) {
                itemData.put("customPotionName", customPotionName);
            }
        }

        // Custom Effects
        if (!potionMeta.getCustomEffects().isEmpty()) {
            Map<String, Object> effects = new HashMap<>();
            for (PotionEffect customEffect : potionMeta.getCustomEffects()) {
                effects.put(customEffect.getType().getName(), customEffect.serialize());
            }
            itemData.put("customEffects", effects);
        }
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemStack itemStack, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof PotionMeta))
            return;

        PotionMeta potionMeta = (PotionMeta)meta;

        // Main Effect
        if (itemData.containsKey("basePotionType"))
        {
            String baseType = (String)itemData.get("basePotionType");
            boolean extended = false;
            boolean upgraded = false;
            if (baseType.startsWith("STRONG_")) {
                baseType = baseType.substring(7);
                upgraded = true;
            }
            if (baseType.startsWith("LONG_")) {
                baseType = baseType.substring(5);
                extended = true;
            }

            PotionType potionType = parent.registryAccessor().getPotionType(baseType);
            if (potionType != null) {
                potionMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
            }
        }

        // Custom Potion Name
        if (itemData.containsKey("customPotionName")) {
            String customPotionName = (String)itemData.get("customPotionName");
            parent.potionNameHandler().setCustomName(potionMeta, customPotionName);
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

                PotionEffectType type = parent.registryAccessor().getPotionEffectType(entry.getKey());
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