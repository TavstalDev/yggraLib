package io.github.tavstaldev.yggraLib.impl.v1_21_5;

import io.github.tavstaldev.yggraLib.api.IPlatform;
import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ILanguageService;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.localization.TranslationService;
import io.github.tavstaldev.yggraLib.api.scheduler.IScheduler;
import io.github.tavstaldev.yggraLib.bukkit.gui.IGuiRenderer;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.bukkit.services.*;
import io.github.tavstaldev.yggraLib.bukkit.utils.VersionUtils;
import io.github.tavstaldev.yggraLib.impl.v1_21_5.items.CustomModelDataIO_1_21_5;
import io.github.tavstaldev.yggraLib.impl.v1_21_5.items.RegistryAccessor_1_21_5;
import io.github.tavstaldev.yggraLib.impl.vmodern.items.ItemSerializer_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.scheduler.FoliaScheduler_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.scheduler.PaperScheduler_modern;
import io.github.tavstaldev.yggraLib.impl.vmodern.services.*;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Platform implements IPlatform {
    @SneakyThrows
    public void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection) {
        serviceCollection.put(ILanguageService.class, new LanguageService_modern());
        ITranslationService translationService = new TranslationService(plugin);
        serviceCollection.put(ITranslationService.class, translationService);

        serviceCollection.put(IActionBarService.class, new ActionBarService_modern(plugin, translationService));
        serviceCollection.put(IChatService.class, new ChatService_modern(plugin, translationService));
        serviceCollection.put(IBossBarService.class, new BossBarService_modern());
        serviceCollection.put(ITitleService.class, new TitleService_modern(translationService));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_modern());
        serviceCollection.put(ISoundService.class, new SoundService_modern());
        if (VersionUtils.isFolia()) {
            serviceCollection.put(IScheduler.class, new FoliaScheduler_modern((JavaPlugin) plugin));
        }
        else {
            serviceCollection.put(IScheduler.class, new PaperScheduler_modern((JavaPlugin) plugin));
        }
        serviceCollection.put(ItemSerializerBase.class, new ItemSerializer_modern(Logger.getLogger("ItemSerializer"),
                new RegistryAccessor_1_21_5(), new CustomModelDataIO_1_21_5()));
        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_modern());
    }
}
