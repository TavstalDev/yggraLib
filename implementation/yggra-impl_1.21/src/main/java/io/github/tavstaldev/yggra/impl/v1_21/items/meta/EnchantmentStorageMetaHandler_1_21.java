package io.github.tavstaldev.yggra.impl.v1_21.items.meta;

import io.github.tavstaldev.yggra.api.utils.TypeUtils;
import io.github.tavstaldev.yggra.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggra.bukkit.items.ItemSerializerBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentStorageMetaHandler_1_21 implements IMetaHandler {
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
                Enchantment enchantment = parent.registryAccessor.getEnchantment(entry.getKey());
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
            Enchantment enchantment = parent.registryAccessor.getEnchantment(entry.getKey());
            if (enchantment == null)
                continue;

            enchantmentStorageMeta.addStoredEnchant(enchantment, entry.getValue(), true);
        }
    }
}