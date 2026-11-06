package io.github.tavstaldev.yggra.impl.v1_21_5.items;

import io.github.tavstaldev.yggra.api.utils.TypeUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PersistentDataProvider_1_21_5 {
    public @Nullable PersistentDataContainer getContainer(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer();
    }

    /**
     * Reads all persistent data entries from the item and returns them as a
     * {@code Map<String, Object>} where each value is itself a map containing
     * the entry's type and value (e.g., {@code {"type": "STRING", "value": "foo"}}).
     *
     * @param item the item stack to read from
     * @return a map of namespaced-key-string to type-tagged value entries
     */
    public Map<String, Object> mapPersistentData(ItemStack item) {
        Map<String, Object> persistentData = new HashMap<>();
        var data = getContainer(item);
        if (data == null)
            return persistentData;

        for (var key : data.getKeys()) {
            if (data.has(key, PersistentDataType.STRING)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "STRING",
                                "value", data.get(key, PersistentDataType.STRING)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.INTEGER)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "INTEGER",
                                "value", data.get(key, PersistentDataType.INTEGER)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.DOUBLE)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "DOUBLE",
                                "value", data.get(key, PersistentDataType.DOUBLE)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.FLOAT)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "FLOAT",
                                "value", data.get(key, PersistentDataType.FLOAT)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.LONG)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "LONG",
                                "value", data.get(key, PersistentDataType.LONG)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.BYTE)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "BYTE",
                                "value", data.get(key, PersistentDataType.BYTE)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.BYTE_ARRAY)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "BYTE_ARRAY",
                                "value", data.get(key, PersistentDataType.BYTE_ARRAY)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.INTEGER_ARRAY)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "INTEGER_ARRAY",
                                "value", data.get(key, PersistentDataType.INTEGER_ARRAY)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.LONG_ARRAY)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "LONG_ARRAY",
                                "value", data.get(key, PersistentDataType.LONG_ARRAY)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.SHORT)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "SHORT",
                                "value", data.get(key, PersistentDataType.SHORT)
                        )
                );
                continue;
            }

            if (data.has(key, PersistentDataType.BOOLEAN)) {
                //noinspection DataFlowIssue
                persistentData.put(key.asString(),
                        Map.of("type", "BOOLEAN",
                                "value", data.get(key, PersistentDataType.BOOLEAN)
                        )
                );
                continue;
            }
        }
        return persistentData;
    }

    /**
     * Writes the given type-tagged entries back into the item's
     * {@link PersistentDataContainer}.
     * <p>
     * The map is expected to have the same format as produced by
     * {@link #mapPersistentData(ItemStack)}, i.e. each value is a map with
     * {@code "type"} and {@code "value"} keys.
     *
     * @param item the item stack to write to
     * @param map  the map of namespaced-key-string to type-tagged value entries
     */
    public void setPersistentData(ItemStack item, Map<String, Object> map) {
        var dataContainer = getContainer(item);
        if (dataContainer == null)
            return;

        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            NamespacedKey namespacedKey = NamespacedKey.fromString(key);
            if (namespacedKey == null)
                continue;
            var valueMap = TypeUtils.castAsMap(entry.getValue(), null);
            if (valueMap == null || !valueMap.containsKey("type") || !valueMap.containsKey("value"))
                continue;

            var type = (String) valueMap.get("type");
            var value = valueMap.get("value");
            switch (type) {
                case "STRING" -> {
                    if (value instanceof String stringValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.STRING, stringValue);
                    }
                }
                case "INTEGER" -> {
                    if (value instanceof Integer intValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.INTEGER, intValue);
                    }
                }
                case "DOUBLE" -> {
                    if (value instanceof Double doubleValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.DOUBLE, doubleValue);
                    }
                }
                case "FLOAT" -> {
                    if (value instanceof Float floatValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.FLOAT, floatValue);
                    }
                }
                case "LONG" -> {
                    if (value instanceof Long longValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.LONG, longValue);
                    }
                }
                case "BYTE" -> {
                    if (value instanceof Byte byteValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.BYTE, byteValue);
                    }
                }
                case "BYTE_ARRAY" -> {
                    if (value instanceof byte[] byteArrayValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.BYTE_ARRAY, byteArrayValue);
                    }
                }
                case "INTEGER_ARRAY" -> {
                    if (value instanceof int[] intArrayValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.INTEGER_ARRAY, intArrayValue);
                    }
                }
                case "LONG_ARRAY" -> {
                    if (value instanceof long[] longArrayValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.LONG_ARRAY, longArrayValue);
                    }
                }
                case "SHORT" -> {
                    if (value instanceof Short shortValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.SHORT, shortValue);
                    }
                }
                case "BOOLEAN" -> {
                    if (value instanceof Boolean booleanValue) {
                        dataContainer.set(namespacedKey, PersistentDataType.BOOLEAN, booleanValue);
                    }
                }
            }
        }
    }
}
