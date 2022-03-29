package net.gunivers.vimcraft;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
	MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScreenOpen(ScreenOpenEvent event) {
	if (event.getScreen() instanceof CommandBlockEditScreen originalScreen) {
	    try {
		Field entityField = originalScreen.getClass().getDeclaredField("autoCommandBlock");
		entityField.setAccessible(true);
		event.setScreen(new NoButtonCommandBlockScreen((CommandBlockEntity) entityField.get(originalScreen)));
	    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		LOGGER.error("Error while trying to modify CommandBlockEditScreen", e);
	    }
	}
    }
}
