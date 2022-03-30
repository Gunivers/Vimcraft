package net.gunivers.vimcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.gunivers.vimcraft.NoButtonCommandBlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

@Mixin(ClientPacketListener.class)
public abstract class ClientListenerMixin implements ClientGamePacketListener {

    @Shadow
    private Minecraft minecraft;

    @Inject(method = "lambda$handleBlockEntityData$5", at = @At(value = "RETURN"))
    private void onHandleBlockEntityData(ClientboundBlockEntityDataPacket packet, BlockEntity blockEntity,
	CallbackInfo callback) {
	if (blockEntity instanceof CommandBlockEntity && this.minecraft.screen instanceof NoButtonCommandBlockScreen) {
	    ((NoButtonCommandBlockScreen) this.minecraft.screen).updateGui();
	}
    }

}
