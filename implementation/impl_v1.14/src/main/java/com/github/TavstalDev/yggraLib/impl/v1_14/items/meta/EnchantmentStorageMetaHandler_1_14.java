package com.github.TavstalDev.yggraLib.impl.v1_14.items.meta;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of EnchantmentStorageMetaHandler for Minecraft 1.14.
 */
public class EnchantmentStorageMetaHandler_1_14 implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) {
        if (!(meta instanceof EnchantmentStorageMeta))
            return;

        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
        Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : enchantmentStorageMeta.getEnchants().entrySet()) {
            enchantments.put(entry.getKey().getKey().toString(), entry.getValue());  // Store enchantment names and levels
        }
        itemData.put("enchantmentStorage", enchantments);
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) {
        if (!itemData.containsKey("enchantmentStorage"))
            return;

        if (!(meta instanceof EnchantmentStorageMeta))
            return;

        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
        Object data = itemData.get("enchantmentStorage");
        if (data instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection)data;
            Map<String, Object> enchantments = section.getValues(false);
            if (enchantments.isEmpty())
                return;

            for (Map.Entry<String, Object> entry : enchantments.entrySet()) {
                Enchantment enchantment = parent.registryAccessor().getEnchantment(entry.getKey());
                if (enchantment == null)
                    continue;

                enchantmentStorageMeta.addStoredEnchant(enchantment, (Integer)entry.getValue(), true);
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

            enchantmentStorageMeta.addStoredEnchant(enchantment, entry.getValue(), true);
        }
    }
}