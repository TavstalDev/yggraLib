package com.github.TavstalDev.yggraLib.bukkit.items;

import com.cryptomorin.xseries.XMaterial;
import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.utils.NbtUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * Base class for serializing and deserializing Bukkit item stacks.
 * <p>
 * It supports conversion to maps, YAML, and byte arrays, and it delegates
 * metadata handling to registered handlers.
 */
@SuppressWarnings("unused")
public final class ItemSerializer implements IItemSerializer {
    /** Logger used for serialization and deserialization messages. */
    private final Logger logger;
    /** YAML parser used for item serialization. */
    private final Yaml yamlParser;
    /** Registry accessor used to resolve version-specific values. */
    public final IRegistryAccessor registryAccessor;

    /** Accessor used for item stack operations. */
    public final IItemStackAccessor itemStackAccessor;
    /** Accessor used for item NBT operations, or {@code null} if unavailable. */
    public final INbtAccessor nbtAccessor;

    /** Handler for custom model data. */
    public final ICustomModelDataIO customModelDataIO;
    /** Handler for potion names. */
    public final IPotionNameHandler potionNameHandler;
    /** Mapper used to serialize and deserialize persistent item data. */
    public final IPersistentDataMapper persistentDataMapper;

    /** {@inheritDoc} */
    public IRegistryAccessor registryAccessor() {
        return registryAccessor;
    }
    /** {@inheritDoc} */
    public IPotionNameHandler potionNameHandler() {
        return potionNameHandler;
    }

    /** Metadata handlers grouped by handler type. */
    public final HashMap<EHandlerType, IMetaHandler> metaHandlers;


    public ItemSerializer(Logger logger, IRegistryAccessor registryAccessor,
                          IItemStackAccessor itemStackAccessor, @Nullable INbtAccessor nbtAccessor,
                          ICustomModelDataIO customModelDataIO, IPersistentDataMapper persistentDataMapper,
                          IPotionNameHandler potionNameHandler,
                          HashMap<EHandlerType, IMetaHandler> metaHandlers) {
        this.logger = logger;
        this.registryAccessor = registryAccessor;
        this.itemStackAccessor = itemStackAccessor;
        this.nbtAccessor = nbtAccessor;
        this.customModelDataIO = customModelDataIO;
        this.persistentDataMapper = persistentDataMapper;
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
    public Map<String, Object> serializeItemStack(final @NotNull ItemStack item) {
        Map<String, Object> itemData = new HashMap<>();
        try
        {
            itemData.put("material", item.getType().toString());
            itemData.put("amount", itemStackAccessor.getAmount(item));
            int durability = itemStackAccessor.getDurability(item);
            if (durability >= 0)
                itemData.put("durability", durability);

            if (!item.hasItemMeta())
                return itemData;

            ItemMeta meta = item.getItemMeta();

            // Name
            String displayName = itemStackAccessor.getDisplayName(item);
            if (displayName != null)
                itemData.put("name", displayName);

            // Lore
            List<String> metaLore = itemStackAccessor.getLore(item);
            if (metaLore != null)
                itemData.put("lore", metaLore);

            // Item Meta Handlers
            for (Map.Entry<EHandlerType, IMetaHandler> handler : metaHandlers.entrySet()) {
                try {
                    handler.getValue().serialize(this, item, meta, itemData);
                }
                catch (Exception metaEx) {
                    logger.severe(String.format("An error occurred while serializing %s meta: %s",
                            handler.getKey().name(), metaEx.getMessage()));
                }
            }

            // Add persistent data
            if (persistentDataMapper != null) {
                Map<String, Object> persistentData = persistentDataMapper.map(item);
                if (!persistentData.isEmpty())
                    itemData.put("persistent-data", persistentData);
            }

            // NBT-Data
            if (nbtAccessor != null) {
                Map<String, Object> nbtData = nbtAccessor.getAll(item);
                itemData.put("nbt", nbtData);
            }
            else if (customModelDataIO != null)
                customModelDataIO.serialize(meta, itemData);

        }
        catch (Exception ex) {
            logger.severe("An error occurred while serializing an item: " + ex);
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
        ItemStack itemStack = null;
        try
        {
            String materialString = (String) itemMap.get("material");
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(materialString);
            if (!xMaterial.isPresent()) {
                logger.warning("Unknown material: " + materialString);
                return null;
            }

            Material material = xMaterial.get().get();
            if (material == null)
                return null;

            int amount = itemMap.containsKey("amount") ? (int) itemMap.get("amount") : 1;
            itemStack = new ItemStack(material, amount);

            // Name
            if (itemMap.containsKey("name"))
                itemStackAccessor.setDisplayName(itemStack, (String)itemMap.get("name"));

            // Lore
            if (itemMap.containsKey("lore")) {
                Object loreData = itemMap.get("lore");
                if (loreData instanceof List) {
                    List<String> rawRoles = TypeUtils.castAsList(loreData, null);
                    itemStackAccessor.setLore(itemStack, rawRoles);
                }
            }

            // Durability
            if (itemMap.containsKey("durability")) {
                int durability = 0;
                Object durabilityObj = itemMap.get("durability");
                if (durabilityObj instanceof Number)
                    durability = ((Number)durabilityObj).intValue();
                itemStackAccessor.setDurability(itemStack, durability);
            }

            // Meta Handlers
            ItemMeta meta = itemStack.getItemMeta();
            for (Map.Entry<EHandlerType, IMetaHandler> handler : metaHandlers.entrySet()) {
                try {
                    handler.getValue().deserialize(this, meta, itemMap);
                }
                catch (Exception metaEx) {
                    logger.severe(String.format("An error occurred while deserializing %s meta: %s",
                            handler.getKey().name(), metaEx.getMessage()));
                }
            }

            // CustomModelData
            if (customModelDataIO != null)
                customModelDataIO.deserialize(meta, itemMap);

            itemStack.setItemMeta(meta);

            // Persistent Data Provider
            if (itemMap.containsKey("persistent-data")) {
                Map<String, Object> dataMap = TypeUtils.castAsMap(itemMap.get("persistent-data"), null);
                if (dataMap != null)
                    persistentDataMapper.set(itemStack, dataMap);
            }

            // NBT-Data
            if (nbtAccessor != null && itemMap.containsKey("nbt")) {
                Map<String, Object> nbt = TypeUtils.castAsMap(itemMap.get("nbt"), null);
                if (nbt != null) {
                    for (Map.Entry<String, Object> entry : nbt.entrySet()) {
                        NbtUtils.set(nbtAccessor, itemStack, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        catch (Exception ex) {
            logger.severe("An error occurred while deserializing an item: " + ex);
        }
        return itemStack;
    }

    //#region Serialization Methods
    /**
     * Serializes a list of {@link ItemStack} objects into a list of maps.
     * Each {@link ItemStack} is converted into a map representation containing its data.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A list of maps, where each map represents the serialized data of an {@link ItemStack}.
     */
    public List<Map<String, Object>> serializeItemStackList(@NotNull List<ItemStack> items) {
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            Map<String, Object> itemData = serializeItemStack(item);
            if (!itemData.isEmpty())
                itemList.add(itemData);
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
    public @Nullable String serializeItemStackToYaml(@NotNull ItemStack item) {
        Map<String, Object> itemData = serializeItemStack(item);
        if (!itemData.isEmpty())
            return yamlParser.dump(itemData);
        return null;
    }

    /**
     * Serializes a list of {@link ItemStack} objects into a YAML representation.
     * The YAML string contains the serialized data of the list of {@link ItemStack} objects.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A YAML string representing the serialized list of {@link ItemStack} objects, or null if serialization fails.
     */
    public @Nullable String serializeItemStackListToYaml(@NotNull List<ItemStack> items) {
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
    public byte[] serializeItemStackToBytes(@NotNull ItemStack item) {
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
    public byte[] serializeItemStackListToBytes(@NotNull List<ItemStack> items) {
        List<Map<String, Object>> itemDataList = serializeItemStackList(items);

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
    public List<ItemStack> deserializeItemStackList(@NotNull List<Map<String, Object>> itemList) {
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
    public @Nullable ItemStack deserializeItemStackFromYaml(@NotNull String yaml) {
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
    public List<ItemStack> deserializeItemStackListFromYaml(@NotNull String yaml) {
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
    public ItemStack deserializeItemStackFromBytes(byte @NotNull [] data) {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {

            Object streamObject = objectStream.readObject();
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
    public List<ItemStack> deserializeItemStackListFromBytes(byte @NotNull [] data) {
        List<ItemStack> items = new ArrayList<>();
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {

            Object streamObject = objectStream.readObject();
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
