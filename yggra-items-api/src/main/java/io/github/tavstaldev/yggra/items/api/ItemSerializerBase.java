package io.github.tavstaldev.yggra.items.api;

import io.github.tavstaldev.yggra.items.api.utils.AdventureUtils;
import io.github.tavstaldev.yggra.items.api.utils.TypeUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Base class for serializing and deserializing {@link ItemStack} objects to/from
 * {@code Map<String, Object>}, YAML, and byte array representations.
 * <p>
 * Concrete subclasses provide version-specific implementations for the version-dependent
 * abstractions ({@link IRegistryAccessor}, {@link PersistentDataProviderBase},
 * {@link ICustomModelDataIO}, {@link IPotionNameHandler}) and register the appropriate
 * {@link IMetaHandler} implementations via {@link EHandlerType}.
 */
@SuppressWarnings("unused")
public abstract class ItemSerializerBase {
    /** Logger for serialization/deserialization messages. */
    protected final Logger logger;
    /** SnakeYAML parser configured with block flow style and 2-space indent. */
    protected final Yaml yamlParser;
    /** Version-independent registry accessor. */
    public final IRegistryAccessor registryAccessor;
    /** Persistent data container provider. */
    public final PersistentDataProviderBase persistentDataProvider;
    /** Custom model data component serializer. */
    public final ICustomModelDataIO customModelDataIO;
    /** Potion name handler (version-abstracted). */
    public final IPotionNameHandler potionNameHandler;
    /** Map of metadata handlers keyed by handler type. */
    protected final HashMap<EHandlerType, IMetaHandler> metaHandlers;

    /**
     * Constructs a new serializer base.
     *
     * @param logger                 the logger
     * @param registryAccessor       version-independent registry accessor
     * @param persistentDataProvider persistent data container provider
     * @param customModelDataIO      custom model data component serializer
     * @param potionNameHandler      potion name handler
     * @param metaHandlers           map of metadata handlers keyed by handler type
     */
    public ItemSerializerBase(Logger logger, IRegistryAccessor registryAccessor, PersistentDataProviderBase persistentDataProvider,
                              ICustomModelDataIO customModelDataIO, IPotionNameHandler potionNameHandler,
                              HashMap<EHandlerType, IMetaHandler> metaHandlers) {
        this.logger = logger;
        this.registryAccessor = registryAccessor;
        this.persistentDataProvider = persistentDataProvider;
        this.customModelDataIO = customModelDataIO;
        this.potionNameHandler = potionNameHandler;
        this.metaHandlers = metaHandlers;

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        this.yamlParser = new Yaml(options);
    }

    /**
     * Serializes a single {@link ItemStack} into a {@code Map<String, Object>}.
     * <p>
     * Includes material, amount, display name (as Gson JSON), lore, durability,
     * persistent data, custom model data, and all registered {@link IMetaHandler}
     * metadata.
     *
     * @param item the item stack to serialize
     * @return a map of serialized item data
     */
    public Map<String, Object> serializeItemStack(@NotNull ItemStack item) {
        Map<String, Object> itemData = new HashMap<>();
        logger.fine("Serializing item: " + item.getType().name());
        itemData.put("material", item.getType().toString());  // Store the material (type) of the item
        itemData.put("amount", item.getAmount());  // Store the amount of the item


        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {

                // Add item meta data like display name, lore, etc. (Optional)
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

    /**
     * Deserializes an {@link ItemStack} from a {@code Map<String, Object>}.
     * <p>
     * Restores material, amount, display name (auto-detecting Gson JSON vs.
     * legacy color codes), lore, durability, persistent data, custom model data,
     * and all registered {@link IMetaHandler} metadata.
     *
     * @param itemMap the map of serialized item data
     * @return the deserialized item stack, or {@code null} if deserialization fails
     */
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
                        meta.displayName(AdventureUtils.translateColors(rawName, true));
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
                                    loreList.add(AdventureUtils.translateColors(line, true));
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

                // customModelData
                if (itemMap.containsKey("customModelData")) {
                    customModelDataIO.deserialize(meta, itemMap);
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

                item.setItemMeta(meta);  // Set the meta data to the item
                itemResult = item;
            }
        } catch (Exception ex) {
            logger.severe("An error occurred while deserializing items: " + ex);
        }
        return itemResult;
    }

    //#region Serialization Methods
    /**
     * Serializes a list of {@link ItemStack} objects into a list of maps.
     * Each {@link ItemStack} is converted into a map representation containing its data.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A list of maps, where each map represents the serialized data of an {@link ItemStack}.
     */
    public final List<Map<String, Object>> serializeItemStackList(@NotNull List<ItemStack> items) {
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            Map<String, Object> itemData = serializeItemStack(item);
            if (itemData != null) {
                itemList.add(itemData);
            }
        }
        return itemList;
    }

    /**
     * Serializes a single {@link ItemStack} into its YAML representation.
     * The YAML string contains the serialized data of the {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to serialize.
     * @return A YAML string representing the serialized {@link ItemStack}, or null if serialization fails.
     */
    public final @Nullable String serializeItemStackToYaml(@NotNull ItemStack item) {
        Map<String, Object> itemData = serializeItemStack(item);
        if (itemData != null) {
            return yamlParser.dump(itemData);
        }
        return null;
    }

    /**
     * Serializes a list of {@link ItemStack} objects into a YAML representation.
     * The YAML string contains the serialized data of the list of {@link ItemStack} objects.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A YAML string representing the serialized list of {@link ItemStack} objects, or null if serialization fails.
     */
    public final @Nullable String serializeItemStackListToYaml(@NotNull List<ItemStack> items) {
        List<Map<String, Object>> itemList = serializeItemStackList(items);
        if (!itemList.isEmpty()) {
            return yamlParser.dump(itemList);
        }
        return null;
    }

    /**
     * Serializes a single {@link ItemStack} into a byte array.
     * The byte array contains the serialized data of the {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to serialize.
     * @return A byte array representing the serialized {@link ItemStack}, or an empty array if serialization fails.
     */
    public final byte[] serializeItemStackToBytes(@NotNull ItemStack item) {
        Map<String, Object> itemData = serializeItemStack(item);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(itemData);
            return byteStream.toByteArray();
        } catch (Exception ex) {
            logger.severe("An error occurred while serializing item: " + ex);
        }
        return new byte[0];  // Return empty byte array if serialization fails
    }

    /**
     * Serializes a list of {@link ItemStack} objects into a byte array.
     * The byte array contains the serialized data of the list of {@link ItemStack} objects.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A byte array representing the serialized list of {@link ItemStack} objects, or an empty array if serialization fails.
     */
    public final byte[] serializeItemStackListToBytes(@NotNull List<ItemStack> items) {
        var itemDataList = serializeItemStackList(items);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(itemDataList);
            return byteStream.toByteArray();
        } catch (Exception ex) {
            logger.severe("An error occurred while serializing items: " + ex);
        }
        return new byte[0];  // Return empty byte array if serialization fails
    }
    //#endregion

    //#region Deserialization Methods
    /**
     * Deserializes a list of item data maps into a list of {@link ItemStack} objects.
     * Each map in the input list represents the serialized data of an item.
     *
     * @param itemList A list of maps containing serialized item data.
     * @return A list of {@link ItemStack} objects deserialized from the input data.
     */
    public final List<ItemStack> deserializeItemStackList(@NotNull List<Map<String, Object>> itemList) {
        List<ItemStack> items = new ArrayList<>();
        for (Map<String, Object> itemData : itemList) {
            ItemStack item = deserializeItemStack(itemData);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Deserializes a single {@link ItemStack} from its YAML representation.
     *
     * @param yaml A YAML string containing the serialized item data.
     * @return The deserialized {@link ItemStack}, or null if deserialization fails.
     */
    public final @Nullable ItemStack deserializeItemStackFromYaml(@NotNull String yaml) {
        Map<String, Object> itemData = yamlParser.load(yaml);
        if (itemData != null) {
            return deserializeItemStack(itemData);
        }
        return null;
    }

    /**
     * Deserializes a list of {@link ItemStack} objects from their YAML representation.
     *
     * @param yaml A YAML string containing a list of serialized item data.
     * @return A list of {@link ItemStack} objects deserialized from the input YAML.
     */
    public final List<ItemStack> deserializeItemStackListFromYaml(@NotNull String yaml) {
        List<Map<String, Object>> itemList = yamlParser.load(yaml);
        if (itemList != null && !itemList.isEmpty()) {
            return deserializeItemStackList(itemList);
        }
        return new ArrayList<>();
    }

    /**
     * Deserializes a single {@link ItemStack} from a byte array.
     * The byte array must contain serialized item data in a compatible format.
     *
     * @param data A byte array containing the serialized item data.
     * @return The deserialized {@link ItemStack}, or null if deserialization fails.
     */
    public final ItemStack deserializeItemStackFromBytes(byte @NotNull [] data) {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {

            var streamObject = objectStream.readObject();
            if (!(streamObject instanceof Map)) {
                logger.severe("Deserialized object is not a Map.");
                return null;
            }

            Map<String, Object> itemMap = TypeUtils.castAsMap(streamObject, null);
            if (itemMap == null) {
                logger.severe("Deserialized object is not a Map<String, Object>.");
                return null;
            }

            return deserializeItemStack(itemMap);
        } catch (Exception ex) {
            logger.severe("An error occurred while deserializing items: " + ex);
        }
        return null;
    }

    /**
     * Deserializes a list of {@link ItemStack} objects from a byte array.
     * The byte array must contain serialized item data in a compatible format.
     *
     * @param data A byte array containing the serialized list of item data.
     * @return A list of {@link ItemStack} objects deserialized from the input data.
     */
    public final List<ItemStack> deserializeItemStackListFromBytes(byte @NotNull [] data) {
        List<ItemStack> items = new ArrayList<>();
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {

            var streamObject = objectStream.readObject();
            if (!(streamObject instanceof List)) {
                logger.severe("Deserialized object is not a List.");
                return items;
            }

            List<Map<String, Object>> itemDataList = TypeUtils.castAsListOfMaps(streamObject, null);
            if (itemDataList == null) {
                logger.severe("Deserialized object is not a List<Map<String, Object>>.");
                return items;
            }

            items = deserializeItemStackList(itemDataList);
        } catch (Exception ex) {
            logger.severe("An error occurred while deserializing items: " + ex);
        }
        return items;
    }
    //#endregion
}
