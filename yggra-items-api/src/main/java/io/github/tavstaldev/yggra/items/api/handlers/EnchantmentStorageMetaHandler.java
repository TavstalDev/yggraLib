package io.github.tavstaldev.yggra.items.api.handlers;

import io.github.tavstaldev.yggra.items.api.IMetaHandler;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import io.github.tavstaldev.yggra.items.api.utils.TypeUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentStorageMetaHandler implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof EnchantmentStorageMeta enchantmentStorageMeta))
            return;

        Map<String, Integer> enchantments = new HashMap<>();
        for (var entry : enchantmentStorageMeta.getEnchants().entrySet()) {
            enchantments.put(entry.getKey().getKey().toString(), entry.getValue());  // Store enchantment names and levels
        }
        itemData.put("enchantmentStorage", enchantments);
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!itemData.containsKey("enchantmentStorage"))
            return;

        if (!(meta instanceof EnchantmentStorageMeta enchantmentStorageMeta))
            return;

        var data = itemData.get("enchantmentStorage");
        if (data instanceof ConfigurationSection section) {
            Map<String, Object> enchantments = section.getValues(false);
            if (enchantments.isEmpty())
                return;

            for (var entry : enchantments.entrySet()) {
                NamespacedKey namespacedKey;
                if (!entry.getKey().contains(":"))
                    namespacedKey = NamespacedKey.minecraft(entry.getKey().toLowerCase());
                else
                    namespacedKey = NamespacedKey.fromString(entry.getKey());

                if (namespacedKey == null)
                    continue;

                Enchantment enchantment = parent.registryAccessor.getEnchantment(namespacedKey);
                if (enchantment == null)
                    continue;

                enchantmentStorageMeta.addStoredEnchant(enchantment, (Integer)entry.getValue(), true);
            }
            return;
        }

        Map<String, Integer> enchantments = TypeUtils.castAsMap(data, null);
        if (enchantments == null || enchantments.isEmpty())
            return;
        for (var entry : enchantments.entrySet()) {
            NamespacedKey namespacedKey;
            if (!entry.getKey().contains(":"))
                namespacedKey = NamespacedKey.minecraft(entry.getKey().toLowerCase());
            else
                namespacedKey = NamespacedKey.fromString(entry.getKey());
            if (namespacedKey == null)
                continue;

            Enchantment enchantment = parent.registryAccessor.getEnchantment(namespacedKey);
            if (enchantment == null)
                continue;

            enchantmentStorageMeta.addStoredEnchant(enchantment, entry.getValue(), true);
        }
    }
}