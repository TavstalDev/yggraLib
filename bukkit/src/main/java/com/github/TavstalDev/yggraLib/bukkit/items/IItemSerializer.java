package com.github.TavstalDev.yggraLib.bukkit.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface IItemSerializer {

    /**
     * Returns the handler used to access custom potion names across Minecraft versions.
     *
     * @return the potion name handler
     */
    IPotionNameHandler potionNameHandler();

    /**
     * Returns the accessor used to look up Bukkit registry values by key.
     *
     * @return the registry accessor
     */
    IRegistryAccessor registryAccessor();

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
     Map<String, Object> serializeItemStack(@NotNull ItemStack item);

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
    @Nullable ItemStack deserializeItemStack(@NotNull Map<String, Object> itemMap);

    //#region Serialization Methods
    /**
     * Serializes a list of {@link ItemStack} objects into a list of maps.
     * Each {@link ItemStack} is converted into a map representation containing its data.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A list of maps, where each map represents the serialized data of an {@link ItemStack}.
     */
    List<Map<String, Object>> serializeItemStackList(@NotNull List<ItemStack> items);

    /**
     * Serializes a single {@link ItemStack} into its YAML representation.
     * The YAML string contains the serialized data of the {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to serialize.
     * @return A YAML string representing the serialized {@link ItemStack}, or null if serialization fails.
     */
    @Nullable String serializeItemStackToYaml(@NotNull ItemStack item);

    /**
     * Serializes a list of {@link ItemStack} objects into a YAML representation.
     * The YAML string contains the serialized data of the list of {@link ItemStack} objects.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A YAML string representing the serialized list of {@link ItemStack} objects, or null if serialization fails.
     */
    @Nullable String serializeItemStackListToYaml(@NotNull List<ItemStack> items);

    /**
     * Serializes a single {@link ItemStack} into a byte array.
     * The byte array contains the serialized data of the {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to serialize.
     * @return A byte array representing the serialized {@link ItemStack}, or an empty array if serialization fails.
     */
    byte[] serializeItemStackToBytes(@NotNull ItemStack item);

    /**
     * Serializes a list of {@link ItemStack} objects into a byte array.
     * The byte array contains the serialized data of the list of {@link ItemStack} objects.
     *
     * @param items A list of {@link ItemStack} objects to serialize.
     * @return A byte array representing the serialized list of {@link ItemStack} objects, or an empty array if serialization fails.
     */
    byte[] serializeItemStackListToBytes(@NotNull List<ItemStack> items);
    //#endregion

    //#region Deserialization Methods
    /**
     * Deserializes a list of item data maps into a list of {@link ItemStack} objects.
     * Each map in the input list represents the serialized data of an item.
     *
     * @param itemList A list of maps containing serialized item data.
     * @return A list of {@link ItemStack} objects deserialized from the input data.
     */
    List<ItemStack> deserializeItemStackList(@NotNull List<Map<String, Object>> itemList);

    /**
     * Deserializes a single {@link ItemStack} from its YAML representation.
     *
     * @param yaml A YAML string containing the serialized item data.
     * @return The deserialized {@link ItemStack}, or null if deserialization fails.
     */
    @Nullable ItemStack deserializeItemStackFromYaml(@NotNull String yaml);

    /**
     * Deserializes a list of {@link ItemStack} objects from their YAML representation.
     *
     * @param yaml A YAML string containing a list of serialized item data.
     * @return A list of {@link ItemStack} objects deserialized from the input YAML.
     */
    List<ItemStack> deserializeItemStackListFromYaml(@NotNull String yaml);

    /**
     * Deserializes a single {@link ItemStack} from a byte array.
     * The byte array must contain serialized item data in a compatible format.
     *
     * @param data A byte array containing the serialized item data.
     * @return The deserialized {@link ItemStack}, or null if deserialization fails.
     */
    ItemStack deserializeItemStackFromBytes(byte @NotNull [] data);

    /**
     * Deserializes a list of {@link ItemStack} objects from a byte array.
     * The byte array must contain serialized item data in a compatible format.
     *
     * @param data A byte array containing the serialized list of item data.
     * @return A list of {@link ItemStack} objects deserialized from the input data.
     */
    List<ItemStack> deserializeItemStackListFromBytes(byte @NotNull [] data);
    //#endregion
}
