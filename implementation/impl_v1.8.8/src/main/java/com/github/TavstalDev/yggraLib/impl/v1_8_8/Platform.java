package com.github.TavstalDev.yggraLib.impl.v1_8_8;

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
import com.github.TavstalDev.yggraLib.impl.v1_8_8.items.meta.PotionMetaHandler_1_8_8;
import com.github.TavstalDev.yggraLib.impl.vold.items.ItemStackAccessor_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.PotionNameHandler_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.RegistryAccessor_old;
import com.github.TavstalDev.yggraLib.impl.vold.items.meta.*;
import com.github.TavstalDev.yggraLib.impl.vold.items.metaAccessor.SkullMetaAccessor_old;
import com.github.TavstalDev.yggraLib.impl.vold.scheduler.PaperScheduler_old;
import com.github.TavstalDev.yggraLib.impl.vold.services.*;
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
 * Platform implementation for Minecraft 1.8.8.
 * <p>
 * Registers version-specific services and helpers in the service collection.
 */
@SuppressWarnings("unused")
public class Platform implements IPlatform {

    private final HashMap<EHandlerType, IMetaHandler> _metaHandlers = new HashMap<EHandlerType, IMetaHandler>() {{
        put(EHandlerType.BOOK, new BookMetaHandler_old());
        put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler_old());
        put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler_old());
        put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler_old());
        put(EHandlerType.FIREWORK, new FireworkMetaHandler_old());
        put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler_old());
        put(EHandlerType.POTION, new PotionMetaHandler_1_8_8());
        put(EHandlerType.SKULL, new SkullMetaHandler_old());
    }};

    /** {@inheritDoc} */
    @SneakyThrows
    public void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection) {
        @Nullable INbtAccessor nbtAccessor = plugin.getService(INbtAccessor.class);

        serviceCollection.put(ILanguageService.class, new LanguageService_old());
        ITranslationService translationService = new TranslationService(plugin);
        serviceCollection.put(ITranslationService.class, translationService);

        JavaPlugin javaPlugin = (JavaPlugin)plugin;
        serviceCollection.put(IActionBar.class, new YggraActionBar(javaPlugin));
        serviceCollection.put(IChatService.class, new ChatService_old(plugin, translationService));
        serviceCollection.put(IBossBar.class, new YggraBossBar(javaPlugin));
        serviceCollection.put(ITitle.class, new YggraTitle(javaPlugin));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_old());
        serviceCollection.put(ISoundService.class, new SoundService_old());
        serviceCollection.put(IParticle.class, new YggraParticle(javaPlugin));
        serviceCollection.put(IScheduler.class, new PaperScheduler_old(javaPlugin));

        IRegistryAccessor registryAccessor = new RegistryAccessor_old();
        serviceCollection.put(IRegistryAccessor.class, registryAccessor);

        IItemStackAccessor itemStackAccessor = new ItemStackAccessor_old(registryAccessor);
        serviceCollection.put(IItemStackAccessor.class, itemStackAccessor);
        serviceCollection.put(IItemSerializer.class, new ItemSerializer(javaPlugin.getLogger(),
                registryAccessor, itemStackAccessor, nbtAccessor, null, null,
                new PotionNameHandler_old(), _metaHandlers));

        serviceCollection.put(ISkullMetaAccessor.class, new SkullMetaAccessor_old());

        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_old());
    }
}
