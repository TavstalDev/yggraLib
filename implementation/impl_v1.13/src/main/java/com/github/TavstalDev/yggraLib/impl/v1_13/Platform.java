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
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemStackAccessor;
import com.github.TavstalDev.yggraLib.bukkit.items.IRegistryAccessor;
import com.github.TavstalDev.yggraLib.bukkit.items.ItemSerializerBase;
import com.github.TavstalDev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import com.github.TavstalDev.yggraLib.bukkit.services.*;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.ItemSerializer_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.RegistryAccessor_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.items.metaAccessor.SkullMetaAccessor_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.services.LanguageService_1_13;
import com.github.TavstalDev.yggraLib.impl.v1_13.services.SoundService_1_13;
import com.github.TavstalDev.yggraLib.impl.vlegacy.items.ItemStackAccessor_legacy;
import com.github.TavstalDev.yggraLib.impl.vlegacy.scheduler.PaperScheduler_legacy;
import com.github.TavstalDev.yggraLib.impl.vlegacy.services.*;
import com.github.TavstalDev.yggraParticle.api.IParticle;
import com.github.TavstalDev.yggraParticle.bootstrap.YggraParticle;
import com.github.TavstalDev.yggraTitle.api.ITitle;
import com.github.TavstalDev.yggraTitle.bootstrap.YggraTitle;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
/**
 * Platform implementation for Minecraft 1.13.
 * <p>
 * Registers version-specific services and helpers in the service collection.
 */
public class Platform implements IPlatform {
    @SneakyThrows
    /**
     * Sets up and registers platform services.
     * 
     * @param plugin the plugin instance using this platform
     * @param serviceCollection the map used to store service instances
     */
    public void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection) {
        serviceCollection.put(ILanguageService.class, new LanguageService_1_13());
        ITranslationService translationService = new TranslationService(plugin);
        serviceCollection.put(ITranslationService.class, translationService);

        JavaPlugin javaPlugin = (JavaPlugin)plugin;
        serviceCollection.put(IActionBar.class, new YggraActionBar(javaPlugin));
        serviceCollection.put(IChatService.class, new ChatService_legacy(plugin, translationService));
        serviceCollection.put(IBossBar.class, new YggraBossBar(javaPlugin));
        serviceCollection.put(ITitle.class, new YggraTitle(javaPlugin));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_legacy());
        serviceCollection.put(ISoundService.class, new SoundService_1_13());
        serviceCollection.put(IParticle.class, new YggraParticle(javaPlugin));
        serviceCollection.put(IScheduler.class, new PaperScheduler_legacy(javaPlugin));

        IRegistryAccessor registryAccessor = new RegistryAccessor_1_13();
        serviceCollection.put(IRegistryAccessor.class, registryAccessor);

        serviceCollection.put(IItemSerializer.class, new ItemSerializer_1_13(Logger.getLogger("ItemSerializer"),
                registryAccessor));
        serviceCollection.put(IItemStackAccessor.class, new ItemStackAccessor_legacy(registryAccessor));

        serviceCollection.put(ISkullMetaAccessor.class, new SkullMetaAccessor_1_13());

        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_legacy());
    }
}
