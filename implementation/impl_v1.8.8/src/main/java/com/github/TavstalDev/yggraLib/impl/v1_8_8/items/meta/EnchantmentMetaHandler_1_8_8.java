package com.github.TavstalDev.yggraLib.impl.v1_8_8.items.meta;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * A handler for serializing and deserializing the enchantments of an {@link ItemMeta}.
 * This class provides static methods to convert the enchantments of an item into a storable format
 * and reconstruct them from the stored data.
 */
public class EnchantmentMetaHandler_1_8_8 implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) {
        if (!meta.hasEnchants())
            return;

        Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            enchantments.put(entry.getKey().getName(), entry.getValue());  // Store enchantment names and levels
        }
        itemData.put("enchantments", enchantments);
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) {
        if (!itemData.containsKey("enchantments"))
            return;

        Object data = itemData.get("enchantments");
        if (data instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection)data;
            Map<String, Object> enchantments = section.getValues(false);
            if (enchantments.isEmpty())
                return;

            for (Map.Entry<String, Object> entry : enchantments.entrySet()) {
                Enchantment enchantment = parent.registryAccessor().getEnchantment(entry.getKey());
                if (enchantment == null)
                    continue;

                meta.addEnchant(enchantment, (Integer)entry.getValue(), true);
            }
            return;
        }

        Map<String, Integer> enchantments = TypeUtils.castAsMap(data, null);
        if (enchantments == null || enchantments.isEmpty())
            return;
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = parent.registryAccessor().getEnchantment(entry.getKey());
            if (enchantment == null)
                continue;

            meta.addEnchant(enchantment, entry.getValue(), true);
        }
    }
}