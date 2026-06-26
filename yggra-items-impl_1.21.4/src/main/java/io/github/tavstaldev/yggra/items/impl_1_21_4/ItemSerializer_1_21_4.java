package io.github.tavstaldev.yggra.items.impl_1_21_4;

import io.github.tavstaldev.yggra.items.api.EHandlerType;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import io.github.tavstaldev.yggra.items.api.handlers.*;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * {@inheritDoc}
 */
public class ItemSerializer_1_21_4 extends ItemSerializerBase {
    /**
     * {@inheritDoc}
     */
    public ItemSerializer_1_21_4(Logger logger) {
        super(logger,
                new RegistryAccessor_1_21_4(),
                new PersistentDataProvider_1_21_4(),
                new CustomModelDataIO_1_21_4(),
                new PotionNameHandler_1_21_4(),
                new HashMap<>() {{
            put(EHandlerType.BOOK, new BookMetaHandler());
            put(EHandlerType.CROSSBOW, new CrossbowMetaHandler());
            put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler());
            put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler());
            put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler());
            put(EHandlerType.FIREWORK, new FireworkMetaHandler());
            put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler());
            put(EHandlerType.POTION, new PotionMetaHandler());
            put(EHandlerType.SKULL, new SkullMetaHandler());
            put(EHandlerType.SPAWN_EGG, new SpawnEggMetaHandler());
        }});
    }
}
