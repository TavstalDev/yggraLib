package com.github.TavstalDev.yggraLib.bukkit.items;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import org.bukkit.inventory.ItemStack;
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
 * Base class for serializing and deserializing Bukkit item stacks.
 * <p>
 * It supports conversion to maps, YAML, and byte arrays, and it delegates
 * metadata handling to registered handlers.
 */
@SuppressWarnings("unused")
public abstract class ItemSerializerBase {
    /** Logger used for serialization and deserialization messages. */
    protected final Logger logger;
    /** YAML parser used for item serialization. */
    protected final Yaml yamlParser;
    /** Registry accessor used to resolve version-specific values. */
    public final IRegistryAccessor registryAccessor;
    /** Handler for custom model data. */
    public final ICustomModelDataIO customModelDataIO;
    /** Handler for potion names. */
    public final IPotionNameHandler potionNameHandler;
    /** Metadata handlers grouped by handler type. */
    protected final HashMap<EHandlerType, IMetaHandler> metaHandlers;

    /**
     * Constructs a new serializer base.
     *
     * @param logger                 the logger
     * @param registryAccessor       version-independent registry accessor
     * @param customModelDataIO      custom model data component serializer
     * @param potionNameHandler      potion name handler
     * @param metaHandlers           map of metadata handlers keyed by handler type
     */
    public ItemSerializerBase(Logger logger, IRegistryAccessor registryAccessor,
                              ICustomModelDataIO customModelDataIO, IPotionNameHandler potionNameHandler,
                              HashMap<EHandlerType, IMetaHandler> metaHandlers) {
        this.logger = logger;
        this.registryAccessor = registryAccessor;
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
    public abstract Map<String, Object> serializeItemStack(@NotNull ItemStack item);

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
    public abstract @Nullable ItemStack deserializeItemStack(@NotNull Map<String, Object> itemMap);

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
    public final List<ItemStack> deserializeItemStackListFromBytes(byte @NotNull [] data) {
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
