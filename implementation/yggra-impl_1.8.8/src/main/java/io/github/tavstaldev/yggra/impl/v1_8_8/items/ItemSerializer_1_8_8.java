package io.github.tavstaldev.yggra.impl.v1_8_8.items;

import io.github.tavstaldev.yggra.api.utils.TypeUtils;
import io.github.tavstaldev.yggra.bukkit.items.EHandlerType;
import io.github.tavstaldev.yggra.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggra.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggra.impl.v1_8_8.items.meta.*;
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
public class ItemSerializer_1_8_8 extends ItemSerializerBase {
    /**
     * {@inheritDoc}
     */
    public ItemSerializer_1_8_8(Logger logger) {
        super(logger,
                new RegistryAccessor_1_8_8(),
                null,
                new PotionNameHandler_1_8_8(),
                createHandlerMap()
        );
    }

    private static HashMap<EHandlerType, IMetaHandler> createHandlerMap() {
        HashMap<EHandlerType, IMetaHandler> map = new HashMap<>();
        map.put(EHandlerType.BOOK, new BookMetaHandler_1_8_8());
        map.put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler_1_8_8());
        map.put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler_1_8_8());
        map.put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler_1_8_8());
        map.put(EHandlerType.FIREWORK, new FireworkMetaHandler_1_8_8());
        map.put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler_1_8_8());
        map.put(EHandlerType.POTION, new PotionMetaHandler_1_8_8());
        map.put(EHandlerType.SKULL, new SkullMetaHandler_1_8_8());
        return map;
    }

    @Override
    public Map<String, Object> serializeItemStack(@NotNull ItemStack item) {
        Map<String, Object> itemData = new HashMap<>();
        logger.fine("Serializing item: " + item.getType().name());
        itemData.put("material", item.getType().toString());  // Store the material (type) of the item
        itemData.put("amount", item.getAmount());  // Store the amount of the item


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

                // Add durability
                if (item.getType().getMaxDurability() > 0) {
                    itemData.put("durability", item.getDurability());
                }

                for (Map.Entry<EHandlerType, IMetaHandler> handler : metaHandlers.entrySet()) {
                    try {
                        handler.getValue().deserialize(this, meta, itemData);
                    }
                    catch (Exception metaEx) {
                        logger.severe(String.format("An error occurred while deserializing %s meta: %s",
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
                    try {
                        durability = (short) itemMap.get("durability");  // Get the durability of the item
                    } catch (ClassCastException ignored){}
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

                // customModelData
                customModelDataIO.deserialize(meta, itemMap);

                item.setItemMeta(meta);  // Set the meta data to the item
                itemResult = item;
            }
        } catch (Exception ex) {
            logger.severe("An error occurred while deserializing items: " + ex);
        }
        return itemResult;
    }
}
