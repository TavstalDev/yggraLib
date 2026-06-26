package io.github.tavstaldev.yggra.items.api.handlers;

import io.github.tavstaldev.yggra.items.api.IMetaHandler;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import io.github.tavstaldev.yggra.items.api.utils.TypeUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A handler for serializing and deserializing the metadata of a {@link BookMeta}.
 * This class provides static methods to convert the title, author, and pages of a book
 * into a storable format and reconstruct them from the stored data.
 */
public class BookMetaHandler implements IMetaHandler {

    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof BookMeta bookMeta))
            return;

        itemData.put("title", bookMeta.getTitle());
        itemData.put("author", bookMeta.getAuthor());
        List<String> pages = new ArrayList<>();
        for (Component page : bookMeta.pages()) {
            pages.add(GsonComponentSerializer.gson().serialize(page));
        }
        itemData.put("pages", pages);
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof BookMeta bookMeta))
            return;

        if (itemData.containsKey("title"))
            bookMeta.setTitle((String) itemData.get("title"));
        if (itemData.containsKey("author"))
            bookMeta.setAuthor((String) itemData.get("author"));
        if (itemData.containsKey("pages")) {
            var rawPages = itemData.get("pages");
            List<String> pages = TypeUtils.castAsList(rawPages, null);
            if (pages == null) {
                throw new IllegalArgumentException("Invalid pages data: " + rawPages);
            }
            for (String page : pages) {
                bookMeta.addPages(GsonComponentSerializer.gson().deserialize(page));
            }
        }
    }
}