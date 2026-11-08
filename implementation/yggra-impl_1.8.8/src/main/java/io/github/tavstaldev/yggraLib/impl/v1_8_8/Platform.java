package io.github.tavstaldev.yggraLib.impl.v1_8_8;

import io.github.tavstaldev.yggraLib.api.IPlatform;
import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ILanguageService;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.localization.TranslationService;
import io.github.tavstaldev.yggraLib.api.scheduler.IScheduler;
import io.github.tavstaldev.yggraLib.bukkit.gui.IGuiRenderer;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.bukkit.services.*;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.items.ItemSerializer_1_8_8;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.scheduler.PaperScheduler_1_8_8;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.services.*;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Platform implements IPlatform {
    @SneakyThrows
    public void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection) {
        serviceCollection.put(ILanguageService.class, new LanguageService_1_8_8());
        ITranslationService translationService = new TranslationService(plugin);
        serviceCollection.put(ITranslationService.class, translationService);

        serviceCollection.put(IActionBarService.class, new ActionBarService_1_8_8(plugin, translationService));
        serviceCollection.put(IChatService.class, new ChatService_1_8_8(plugin, translationService));
        serviceCollection.put(IBossBarService.class, new BossBarService_1_8_8(plugin));
        serviceCollection.put(ITitleService.class, new TitleService_1_8_8(plugin, translationService));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_1_8_8());
        serviceCollection.put(ISoundService.class, new SoundService_1_8_8(plugin));
        serviceCollection.put(IScheduler.class, new PaperScheduler_1_8_8((JavaPlugin)plugin));
        serviceCollection.put(ItemSerializerBase.class, new ItemSerializer_1_8_8(Logger.getLogger("ItemSerializer")));
        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_1_8_8());
    }
}
