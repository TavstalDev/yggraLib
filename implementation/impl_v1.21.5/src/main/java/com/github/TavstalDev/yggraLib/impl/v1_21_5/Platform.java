package com.github.TavstalDev.yggraLib.impl.v1_21_5;

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
import com.github.TavstalDev.yggraLib.bukkit.services.*;
import com.github.TavstalDev.yggraLib.bukkit.utils.VersionUtils;
import com.github.TavstalDev.yggraLib.impl.v1_21_5.items.CustomModelDataIO_1_21_5;
import com.github.TavstalDev.yggraLib.impl.v1_21_5.items.ItemStackAccessor_1_21_5;
import com.github.TavstalDev.yggraLib.impl.v1_21_5.items.RegistryAccessor_1_21_5;
import com.github.TavstalDev.yggraLib.impl.vmodern.items.PersistentDataProvider_modern;
import com.github.TavstalDev.yggraLib.impl.vmodern.items.PotionNameHandler_modern;
import com.github.TavstalDev.yggraLib.impl.vmodern.items.meta.*;
import com.github.TavstalDev.yggraLib.impl.vmodern.items.metaAccessor.SkullMetaAccessor_modern;
import com.github.TavstalDev.yggraLib.impl.vmodern.scheduler.FoliaScheduler_modern;
import com.github.TavstalDev.yggraLib.impl.vmodern.scheduler.PaperScheduler_modern;
import com.github.TavstalDev.yggraLib.impl.vmodern.services.*;
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
 * Platform implementation for Minecraft 1.21.5.
 * <p>
 * Registers version-specific services and helpers in the service collection.
 */
@SuppressWarnings("unused")
public class Platform implements IPlatform {

    private final HashMap<EHandlerType, IMetaHandler> _metaHandlers = new HashMap<>() {{
        put(EHandlerType.BOOK, new BookMetaHandler_modern());
        put(EHandlerType.CROSSBOW, new CrossbowMetaHandler_modern());
        put(EHandlerType.ENCHANTMENT, new EnchantmentMetaHandler_modern());
        put(EHandlerType.ENCHANTMENT_STORAGE, new EnchantmentStorageMetaHandler_modern());
        put(EHandlerType.FIREWORK_EFFECT, new FireworkEffectMetaHandler_modern());
        put(EHandlerType.FIREWORK, new FireworkMetaHandler_modern());
        put(EHandlerType.LEATHER_ARMOR, new LeatherArmorMetaHandler_modern());
        put(EHandlerType.POTION, new PotionMetaHandler_modern());
        put(EHandlerType.SKULL, new SkullMetaHandler_modern());
        put(EHandlerType.SPAWN_EGG, new SpawnEggMetaHandler_modern());
    }};

    /**
     * Sets up and registers platform services.
     * 
     * @param plugin the plugin instance using this platform
     * @param serviceCollection the map used to store service instances
     */
    @SneakyThrows
    public void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection) {
        @Nullable INbtAccessor nbtAccessor = plugin.getService(INbtAccessor.class);

        serviceCollection.put(ILanguageService.class, new LanguageService_modern());
        ITranslationService translationService = new TranslationService(plugin);
        serviceCollection.put(ITranslationService.class, translationService);

        JavaPlugin javaPlugin = (JavaPlugin)plugin;
        serviceCollection.put(IActionBar.class, new YggraActionBar(javaPlugin));
        serviceCollection.put(IChatService.class, new ChatService_modern(plugin, translationService));
        serviceCollection.put(IBossBar.class, new YggraBossBar(javaPlugin));
        serviceCollection.put(ITitle.class, new YggraTitle(javaPlugin));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_modern());
        serviceCollection.put(ISoundService.class, new SoundService_modern());
        serviceCollection.put(IParticle.class, new YggraParticle(javaPlugin));

        if (VersionUtils.isFolia()) {
            serviceCollection.put(IScheduler.class, new FoliaScheduler_modern(javaPlugin));
        }
        else {
            serviceCollection.put(IScheduler.class, new PaperScheduler_modern(javaPlugin));
        }

        IRegistryAccessor registryAccessor = new RegistryAccessor_1_21_5();
        serviceCollection.put(IRegistryAccessor.class, registryAccessor);
        ICustomModelDataIO customModelDataIO = new CustomModelDataIO_1_21_5();
        serviceCollection.put(ICustomModelDataIO.class, customModelDataIO);
        IItemStackAccessor itemStackAccessor = new ItemStackAccessor_1_21_5(registryAccessor);
        serviceCollection.put(IItemStackAccessor.class, itemStackAccessor);
        serviceCollection.put(IItemSerializer.class, new ItemSerializer(javaPlugin.getLogger(),
                registryAccessor, itemStackAccessor, nbtAccessor, customModelDataIO,  new PersistentDataProvider_modern(),
                new PotionNameHandler_modern(), _metaHandlers));

        serviceCollection.put(ISkullMetaAccessor.class, new SkullMetaAccessor_modern());

        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_modern());
    }
}
