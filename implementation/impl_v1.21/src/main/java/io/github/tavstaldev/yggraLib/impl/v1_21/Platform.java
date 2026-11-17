package io.github.tavstaldev.yggraLib.impl.v1_21;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import io.github.tavstaldev.yggraActionBar.bootstrap.YggraActionBar;
import io.github.tavstaldev.yggraBossBar.api.IBossBar;
import io.github.tavstaldev.yggraBossBar.bootstrap.YggraBossBar;
import io.github.tavstaldev.yggraLib.api.IPlatform;
import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ILanguageService;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.localization.TranslationService;
import io.github.tavstaldev.yggraLib.api.scheduler.IScheduler;
import io.github.tavstaldev.yggraLib.bukkit.gui.IGuiRenderer;
import io.github.tavstaldev.yggraLib.bukkit.items.ICustomModelDataIO;
import io.github.tavstaldev.yggraLib.bukkit.items.IItemStackAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import io.github.tavstaldev.yggraLib.bukkit.services.*;
import io.github.tavstaldev.yggraLib.bukkit.utils.VersionUtils;
import io.github.tavstaldev.yggraLib.impl.v1_21.items.CustomModelDataIO_1_21;
import io.github.tavstaldev.yggraLib.impl.v1_21.items.ItemStackAccessor_1_21;
import io.github.tavstaldev.yggraLib.impl.v1_21.items.RegistryAccessor_1_21;
import io.github.tavstaldev.yggraLib.impl.vmodern.items.ItemSerializer_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.items.metaAccessor.SkullMetaAccessor_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.scheduler.FoliaScheduler_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.scheduler.PaperScheduler_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.services.*;
import io.github.tavstaldev.yggraParticle.api.IParticle;
import io.github.tavstaldev.yggraParticle.bootstrap.YggraParticle;
import io.github.tavstaldev.yggraTitle.api.ITitle;
import io.github.tavstaldev.yggraTitle.bootstrap.YggraTitle;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
/**
 * Platform implementation for Minecraft 1.21.
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

        IRegistryAccessor registryAccessor = new RegistryAccessor_1_21();
        serviceCollection.put(IRegistryAccessor.class, registryAccessor);
        ICustomModelDataIO customModelDataIO = new CustomModelDataIO_1_21();
        serviceCollection.put(ICustomModelDataIO.class, customModelDataIO);
        serviceCollection.put(ItemSerializerBase.class, new ItemSerializer_modern(Logger.getLogger("ItemSerializer"),
                registryAccessor, customModelDataIO));
        serviceCollection.put(IItemStackAccessor.class, new ItemStackAccessor_1_21(registryAccessor));

        serviceCollection.put(ISkullMetaAccessor.class, new SkullMetaAccessor_modern());

        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_modern());
    }
}
