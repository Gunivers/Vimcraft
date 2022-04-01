package net.gunivers.vimcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.gunivers.vimcraft.NoButtonCommandBlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

@Mixin(ClientPacketListener.class)
public abstract class ClientListenerMixin implements ClientGamePacketListener {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    private Connection connection;

    @Override
    @Overwrite
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket packet) {
        PacketUtils.ensureRunningOnSameThread(packet, this, this.minecraft);
        BlockPos blockpos = packet.getPos();
        this.minecraft.level.getBlockEntity(blockpos, packet.getType()).ifPresent((blockEntity) -> {
            blockEntity.onDataPacket(connection, packet);

            if (blockEntity instanceof CommandBlockEntity) {
                if (this.minecraft.screen instanceof CommandBlockEditScreen) {
                    ((CommandBlockEditScreen) this.minecraft.screen).updateGui();
                } else if (this.minecraft.screen instanceof NoButtonCommandBlockScreen) {
                    ((NoButtonCommandBlockScreen) this.minecraft.screen).updateGui();
                }
            }
        });
    }

}
