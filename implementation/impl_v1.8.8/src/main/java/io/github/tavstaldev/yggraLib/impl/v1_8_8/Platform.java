package io.github.tavstaldev.yggraLib.impl.v1_8_8;

import io.github.tavstaldev.yggraLib.api.IPlatform;
import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ILanguageService;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.localization.TranslationService;
import io.github.tavstaldev.yggraLib.api.scheduler.IScheduler;
import io.github.tavstaldev.yggraLib.bukkit.gui.IGuiRenderer;
import io.github.tavstaldev.yggraLib.bukkit.items.IItemStackAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import io.github.tavstaldev.yggraLib.bukkit.services.*;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.items.ItemSerializer_1_8_8;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.items.RegistryAccessor_1_8_8;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.items.metaAccessor.SkullMetaAccessor_1_8_8;
import io.github.tavstaldev.yggraLib.impl.v1_8_8.services.LanguageService_1_8_8;
import io.github.tavstaldev.yggraLib.impl.vlegacy.items.ItemStackAccessor_legacy;
import io.github.tavstaldev.yggraLib.impl.vlegacy.scheduler.PaperScheduler_legacy;
import io.github.tavstaldev.yggraLib.impl.vlegacy.services.*;
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

        serviceCollection.put(IActionBarService.class, new ActionBarService_legacy(plugin, translationService));
        serviceCollection.put(IChatService.class, new ChatService_legacy(plugin, translationService));
        serviceCollection.put(IBossBarService.class, new BossBarService_legacy(plugin));
        serviceCollection.put(ITitleService.class, new TitleService_legacy(plugin, translationService));
        serviceCollection.put(IScoreboardService.class, new ScoreboardService_legacy());
        serviceCollection.put(ISoundService.class, new SoundService_legacy(plugin));
        serviceCollection.put(IScheduler.class, new PaperScheduler_legacy((JavaPlugin)plugin));

        IRegistryAccessor registryAccessor = new RegistryAccessor_1_8_8();
        serviceCollection.put(IRegistryAccessor.class, registryAccessor);

        serviceCollection.put(ItemSerializerBase.class, new ItemSerializer_1_8_8(Logger.getLogger("ItemSerializer"),
                registryAccessor));
        serviceCollection.put(IItemStackAccessor.class, new ItemStackAccessor_legacy(registryAccessor));

        serviceCollection.put(ISkullMetaAccessor.class, new SkullMetaAccessor_1_8_8());

        serviceCollection.put(IGuiRenderer.class, new GuiRenderer_legacy());
    }
}
