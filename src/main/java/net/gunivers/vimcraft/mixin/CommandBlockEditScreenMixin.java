package net.gunivers.vimcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

@Mixin(CommandBlockEditScreen.class)
public interface CommandBlockEditScreenMixin {

    @Accessor
    CommandBlockEntity getAutoCommandBlock();

}
