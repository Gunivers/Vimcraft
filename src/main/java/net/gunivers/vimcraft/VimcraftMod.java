package net.gunivers.vimcraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("vimcraft")
public class VimcraftMod {

    private static final Logger LOGGER = LogUtils.getLogger();

    public VimcraftMod() {
	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

}
