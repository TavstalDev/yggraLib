package com.github.TavstalDev.yggraLib.impl.vmodern.items.meta;

import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
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
public class BookMetaHandler_modern implements IMetaHandler {

    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) throws Exception {
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

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof BookMeta bookMeta))
            return;

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
                bookMeta.addPages(GsonComponentSerializer.gson().deserialize(page));
            }
        }
    }
}