package io.github.tavstaldev.yggraLib.impl.v1_12.items;

import io.github.tavstaldev.yggraLib.api.utils.TypeUtils;
import io.github.tavstaldev.yggraLib.bukkit.items.EHandlerType;
import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.impl.v1_12.items.meta.EnchantmentMetaHandler_1_12;
import io.github.tavstaldev.yggraLib.impl.v1_12.items.meta.EnchantmentStorageMetaHandler_1_12;
import io.github.tavstaldev.yggraLib.impl.v1_12.items.meta.SkullMetaHandler_1_12;
import io.github.tavstaldev.yggraLib.impl.vlegacy.items.PotionNameHandler_legacy;
import io.github.tavstaldev.yggraLib.impl.vlegacy.items.meta.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * {@inheritDoc}
 */
public class ItemSerializer_1_12 extends ItemSerializerBase {
    /**
     * {@inheritDoc}
     */
    public ItemSerializer_1_12(Logger logger, IRegistryAccessor registryAccessor) {
        super(logger,
                registryAccessor,
                null,
                new PotionNameHandler_legacy(),
                createHandlerMap()
        );
    }

    private static HashMap<EHandlerType, IMetaHandler> createHandlerMap() {
        HashMap<EHandlerType, IMetaHandler> map = new HashMap<>();
        map.put(EHandlerType.BOOK, new BookMetaHandler_legacy());
        map.put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler_1_12());
        map.put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler_1_12());
        map.put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler_legacy());
        map.put(EHandlerType.FIREWORK, new FireworkMetaHandler_legacy());
        map.put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler_legacy());
        map.put(EHandlerType.POTION, new PotionMetaHandler_legacy());
        map.put(EHandlerType.SKULL, new SkullMetaHandler_1_12());
        return map;
    }

    @Override
    public Map<String, Object> serializeItemStack(@NotNull ItemStack item) {
        Map<String, Object> itemData = new HashMap<>();
        logger.fine("Serializing item: " + item.getType().name());
        itemData.put("material", item.getType().toString());  // Store the material (type) of the item
        itemData.put("amount", item.getAmount());  // Store the amount of the item
        itemData.put("durability", item.getDurability());

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {

                // Add item metadata like display name, lore, etc. (Optional)
                String displayName = meta.getDisplayName();
                if (displayName != null)
                    itemData.put("name", displayName);
                List<String> metaLore = meta.getLore();
                if (metaLore != null) {
                    itemData.put("lore", metaLore);
                }

                for (Map.Entry<EHandlerType, IMetaHandler> handler : metaHandlers.entrySet()) {
                    try {
                        handler.getValue().serialize(this, meta, itemData);
                    }
                    catch (Exception metaEx) {
                        logger.severe(String.format("An error occurred while serializing %s meta: %s",
                                handler.getKey().name(), metaEx.getMessage()));
                    }
                }
            }
        }

        return itemData;
    }

    @Override
    public @Nullable ItemStack deserializeItemStack(@NotNull Map<String, Object> itemMap) {
        ItemStack itemResult = null;
        try {
            String materialString = (String) itemMap.get("material");
            Material material = Material.getMaterial(materialString);  // Convert material string to Material enum
            int amount;
            if (itemMap.containsKey("amount")) {
                amount = (int) itemMap.get("amount");  // Get the amount of the item
            } else {
                amount = 1;  // Default amount if not specified
            }

            if (material != null) {
                ItemStack item = new ItemStack(material, amount);
                ItemMeta meta = item.getItemMeta();

                // if there is no name, then it does not have metadata.
                if (itemMap.containsKey("name")) {
                    String rawName = (String) itemMap.get("name");
                    meta.setDisplayName(rawName);
                }

                // Lore
                if (itemMap.containsKey("lore")) {
                    Object loreData = itemMap.get("lore");
                    if (loreData instanceof List) {
                        List<String> rawRoles = TypeUtils.castAsList(loreData, null);
                        meta.setLore(rawRoles);
                    }
                }

                // Durability
                if (itemMap.containsKey("durability")) {
                    short durability = 0;
                    Object durabilityObj = itemMap.get("durability");
                    if (durabilityObj instanceof Number) {
                        durability = ((Number)durabilityObj).shortValue();
                    }
                    item.setDurability(durability);
                }

                for (Map.Entry<EHandlerType, IMetaHandler> handler : metaHandlers.entrySet()) {
                    try {
                        handler.getValue().deserialize(this, meta, itemMap);
                    }
                    catch (Exception metaEx) {
                        logger.severe(String.format("An error occurred while deserializing %s meta: %s",
                                handler.getKey().name(), metaEx.getMessage()));
                    }
                }


                item.setItemMeta(meta);  // Set the metadata to the item
                itemResult = item;
            }
        } catch (Exception ex) {
            logger.severe("An error occurred while deserializing items: " + ex);
        }
        return itemResult;
    }
}
