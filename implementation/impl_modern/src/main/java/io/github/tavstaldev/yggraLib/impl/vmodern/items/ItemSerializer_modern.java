package io.github.tavstaldev.yggraLib.impl.vmodern.items;

import io.github.tavstaldev.yggraLib.api.utils.TypeUtils;
import io.github.tavstaldev.yggraLib.bukkit.items.EHandlerType;
import io.github.tavstaldev.yggraLib.bukkit.items.ICustomModelDataIO;
import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import io.github.tavstaldev.yggraLib.impl.vmodern.items.meta.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**

 * Modern implementation of ItemSerializer for newer Minecraft versions.

 */
public class ItemSerializer_modern extends ItemSerializerBase {
    private final PersistentDataProvider_modern persistentDataProvider = new PersistentDataProvider_modern();

    /**

     * Creates a new ItemSerializer_modern instance.
 */
Logger logger, IRegistryAccessor registryAccessor, ICustomModelDataIO customModelDataIO) {
        super(logger,
                registryAccessor,
                customModelDataIO,
                new PotionNameHandler_modern(),
                new HashMap<>() {{
            put(EHandlerType.BOOK, new BookMetaHandler_modern());
            put(EHandlerType.CROSSBOW, new CrossbowMetaHandler_modern());
            put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler_modern());
            put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler_modern());
            put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler_modern());
            put(EHandlerType.FIREWORK, new FireworkMetaHandler_modern());
            put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler_modern());
            put(EHandlerType.POTION, new PotionMetaHandler_modern());
            put(EHandlerType.SKULL, new SkullMetaHandler_modern());
            put(EHandlerType.SPAWN_EGG, new SpawnEggMetaHandler_modern());
        }});
    }
    /**
     * Handles the serializeItemStack operation.
     * @param item the item value
     * @return the result
     */

    @Override
    public Map<String, Object> serializeItemStack(@NotNull ItemStack item) {
        Map<String, Object> itemData = new HashMap<>();
        logger.fine("Serializing item: " + item.getType().name());
        itemData.put("material", item.getType().name());  // Store the material (type) of the item
        itemData.put("amount", item.getAmount());  // Store the amount of the item


        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {

                // Add item metadata like display name, lore, etc. (Optional)
                var displayName = meta.displayName();
                if (displayName != null)
                    itemData.put("name", GsonComponentSerializer.gson().serialize(displayName));
                var metaLore = meta.lore();
                if (metaLore != null) {
                    List<String> lore = new ArrayList<>();
                    for (Component line : metaLore) {
                        lore.add(GsonComponentSerializer.gson().serialize(line));
                    }

                    itemData.put("lore", lore);
                }

                // Add durability
                if (meta instanceof Damageable) {
                    itemData.put("durability", ((Damageable) meta).getDamage());
                }

                // Add persistent data
                Map<String, Object> persistentData = persistentDataProvider.mapPersistentData(item);
                if (!persistentData.isEmpty()) {
                    itemData.put("persistent-data", persistentData);
                }

                // Add nbt tags
                customModelDataIO.serialize(meta, itemData);

                for (var handler : metaHandlers.entrySet()) {
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
                    var rawName = (String) itemMap.get("name");
                    if (rawName.contains("{") && rawName.contains("}")) {
                        meta.displayName(GsonComponentSerializer.gson().deserialize(rawName));
                    } else {
                        meta.displayName(AdventureUtils.parse(rawName, true));
                    }
                }

                // Lore
                if (itemMap.containsKey("lore")) {
                    var loreData = itemMap.get("lore");
                    if (loreData instanceof List) {
                        List<String> rawRoles = TypeUtils.castAsList(loreData, null);
                        List<Component> loreList = new ArrayList<>();
                        if (rawRoles != null) {
                            for (String line : rawRoles) {
                                if (line.contains("{") && line.contains("}")) {
                                    loreList.add(GsonComponentSerializer.gson().deserialize(line));
                                } else {
                                    loreList.add(AdventureUtils.parse(line, true));
                                }
                            }
                        }
                        meta.lore(loreList);
                    }
                }

                // Durability
                if (itemMap.containsKey("durability")) {
                    if (meta instanceof Damageable damageableMeta) {
                        int durability = 0;
                        try {
                            durability = (int) itemMap.get("durability");  // Get the durability of the item
                        } catch (ClassCastException ignored){}
                        damageableMeta.setDamage(durability);
                    }
                }

                if (itemMap.containsKey("persistent-data")) {
                    Map<String, Object> dataMap = TypeUtils.castAsMap(itemMap.get("persistent-data"), null);
                    if (dataMap != null)
                        persistentDataProvider.setPersistentData(item, dataMap);
                }

                for (var handler : metaHandlers.entrySet()) {
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

                item.setItemMeta(meta);  // Set the metadata to the item
                itemResult = item;
            }
        } catch (Exception ex) {
            logger.severe("An error occurred while deserializing items: " + ex);
        }
        return itemResult;
    }
}
