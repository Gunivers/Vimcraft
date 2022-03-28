package net.gunivers.vimcraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

    private boolean isCommandBlock(Block block) {
	return block.equals(Blocks.COMMAND_BLOCK) || block.equals(Blocks.CHAIN_COMMAND_BLOCK)
	    || block.equals(Blocks.REPEATING_COMMAND_BLOCK);
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
	Level world = event.getWorld();

	if (!world.isClientSide)
	    return;

	BlockPos pos = event.getPos();
	BlockState blockState = event.getWorld().getBlockState(pos);

	Minecraft minecraft = Minecraft.getInstance();
	if (isCommandBlock(blockState.getBlock()) && event.getPlayer().equals(minecraft.player)) {

	    BlockEntity blockentity = world.getBlockEntity(pos);
	    if (blockentity instanceof CommandBlockEntity commandBlockEntity
		&& minecraft.player.canUseGameMasterBlocks()) {
		event.setCancellationResult(InteractionResult.SUCCESS);
		minecraft.setScreen(new NoButtonCommandBlockScreen(commandBlockEntity));
		LOGGER.info("Open custom Command Block Screen");
	    } else {
		event.setCancellationResult(InteractionResult.FAIL);
	    }
	}
    }

}
