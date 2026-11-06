package io.github.tavstaldev.yggra.impl.v1_8_8.items.meta;

import io.github.tavstaldev.yggra.api.utils.TypeUtils;
import io.github.tavstaldev.yggra.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggra.bukkit.items.ItemSerializerBase;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * A handler for serializing and deserializing the metadata of a {@link BookMeta}.
 * This class provides static methods to convert the title, author, and pages of a book
 * into a storable format and reconstruct them from the stored data.
 */
public class BookMetaHandler_1_8_8 implements IMetaHandler {

    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof BookMeta))
            return;

        BookMeta bookMeta = (BookMeta)meta;

        itemData.put("title", bookMeta.getTitle());
        itemData.put("author", bookMeta.getAuthor());
        itemData.put("pages", bookMeta.getPages());
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof BookMeta))
            return;

        BookMeta bookMeta = (BookMeta)meta;

        if (itemData.containsKey("title"))
            bookMeta.setTitle((String) itemData.get("title"));
        if (itemData.containsKey("author"))
            bookMeta.setAuthor((String) itemData.get("author"));
        if (itemData.containsKey("pages")) {
            List<String> pages = TypeUtils.castAsList(itemData.get("pages"), null);
            if (pages == null) {
                throw new IllegalArgumentException("Invalid pages data: " + itemData.get("pages"));
            }

            for (String page : pages) {
                bookMeta.addPage(page);
            }
        }
    }
}