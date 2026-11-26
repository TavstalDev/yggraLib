package com.github.TavstalDev.yggraLib.impl.v1_13;

import com.github.TavstalDev.yggraActionBar.api.IActionBar;
import com.github.TavstalDev.yggraActionBar.bootstrap.YggraActionBar;
import com.github.TavstalDev.yggraBossBar.api.IBossBar;
import com.github.TavstalDev.yggraBossBar.bootstrap.YggraBossBar;
import com.github.TavstalDev.yggraLib.api.IPlatform;
import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.localization.ILanguageService;
import com.github.TavstalDev.yggraLib.api.localization.ITranslationService;
import com.github.TavstalDev.yggraLib.api.localization.TranslationService;
import com.github.TavstalDev.yggraLib.api.scheduler.IScheduler;
import com.github.TavstalDev.yggraLib.bukkit.gui.IGuiRenderer;
import com.github.TavstalDev.yggraLib.bukkit.items.*;
import com.github.TavstalDev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import com.github.TavstalDev.yggraLib.bukkit.services.IChatService;
import com.github.TavstalDev.yggraLib.bukkit.services.IScoreboardService;
import com.github.TavstalDev.yggraLib.bukkit.services.ISoundService;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.meta.EnchantmentMetaHandler_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.meta.EnchantmentStorageMetaHandler_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.meta.SkullMetaHandler_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.metaAccessor.SkullMetaAccessor_1_13;
import com.github.TavstalDev.yggraLib.impl.vlegacy.items.meta.PotionMetaHandler_legacy;
import com.github.TavstalDev.yggraLib.impl.vlegacy.services.LanguageService_legacy;
import com.github.TavstalDev.yggraLib.impl.vlegacy.services.SoundService_legacy;
import com.github.TavstalDev.yggraLib.impl.vold.items.ItemStackAccessor_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.PotionNameHandler_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.RegistryAccessor_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.meta.BookMetaHandler_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.meta.FireworkEffectMetaHandler_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.meta.FireworkMetaHandler_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.meta.LeatherArmorMetaHandler_old;
import com.github.TavstalDev.yggraLib.impl.vold.scheduler.PaperScheduler_old;
import com.github.TavstalDev.yggraLib.impl.vold.services.ChatService_old;
import com.github.TavstalDev.yggraLib.impl.vold.services.GuiRenderer_old;
import com.github.TavstalDev.yggraLib.impl.vold.services.ScoreboardService_old;
import com.github.TavstalDev.yggraParticle.api.IParticle;
import com.github.TavstalDev.yggraParticle.bootstrap.YggraParticle;
import com.github.TavstalDev.yggraTitle.api.ITitle;
import com.github.TavstalDev.yggraTitle.bootstrap.YggraTitle;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * Platform implementation for Minecraft 1.13.
 * <p>
 * Registers version-specific services and helpers in the service collection.
 */
@SuppressWarnings("unused")
public class Platform implements IPlatform {

    private final HashMap<EHandlerType, IMetaHandler> _metaHandlers = new HashMap<EHandlerType, IMetaHandler>() {{
        put(EHandlerType.BOOK, new BookMetaHandler_old());
        put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler_1_13());
        put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler_1_13());
        put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler_old());
        put(EHandlerType.FIREWORK, new FireworkMetaHandler_old());
        put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler_old());
        put(EHandlerType.POTION, new PotionMetaHandler_legacy());
        put(EHandlerType.SKULL, new SkullMetaHandler_1_13());
    }};

    /** {@inheritDoc} */
    @SneakyThrows
    public void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection) {
        @Nullable INbtAccessor nbtAccessor = plugin.getService(INbtAccessor.class);

        serviceCollection.put(ILanguageService.class, new LanguageService_legacy());
        ITranslationService translationService = new TranslationService(plugin);
        serviceCollection.put(ITranslationService.class, translationService);

        JavaPlugin javaPlugin = (JavaPlugin)plugin;
        serviceCollection.put(IActionBar.class, new YggraActionBar(javaPlugin));
        serviceCollection.put(IChatService.class, new ChatService_old(plugin, translationService));
        serviceCollection.put(IBossBar.class, new YggraBossBar(javaPlugin));
        serviceCollection.put(ITitle.class, new YggraTitle(javaPlugin));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_old());
        serviceCollection.put(ISoundService.class, new SoundService_legacy());
        serviceCollection.put(IParticle.class, new YggraParticle(javaPlugin));
        serviceCollection.put(IScheduler.class, new PaperScheduler_old(javaPlugin));

        IRegistryAccessor registryAccessor = new RegistryAccessor_old();
        serviceCollection.put(IRegistryAccessor.class, registryAccessor);

        IItemStackAccessor itemStackAccessor = new ItemStackAccessor_old(registryAccessor);
        serviceCollection.put(IItemStackAccessor.class, itemStackAccessor);
        serviceCollection.put(IItemSerializer.class, new ItemSerializer(javaPlugin.getLogger(),
                registryAccessor, itemStackAccessor, nbtAccessor, null, null,
                new PotionNameHandler_old(), _metaHandlers));

        serviceCollection.put(ISkullMetaAccessor.class, new SkullMetaAccessor_1_13());

        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_old());
    }
}
