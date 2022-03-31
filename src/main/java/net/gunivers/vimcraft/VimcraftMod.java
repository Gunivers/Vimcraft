package net.gunivers.vimcraft;

import net.gunivers.vimcraft.mixin.CommandBlockEditScreenMixin;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("vimcraft")
public class VimcraftMod {

    public VimcraftMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScreenOpen(ScreenOpenEvent event) {
        if (event.getScreen() instanceof CommandBlockEditScreenMixin originalScreen) {
            event.setScreen(new NoButtonCommandBlockScreen(originalScreen.getAutoCommandBlock()));
        }
    }
}
