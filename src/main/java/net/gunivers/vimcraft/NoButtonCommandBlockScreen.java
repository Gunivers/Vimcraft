package net.gunivers.vimcraft;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class NoButtonCommandBlockScreen extends Screen {

    private static final Component SET_COMMAND_LABEL = new TranslatableComponent("advMode.setCommand");
    private static final Component COMMAND_LABEL = new TranslatableComponent("advMode.command");
    private static final Component PREVIOUS_OUTPUT_LABEL = new TranslatableComponent("advMode.previousOutput");

    protected EditBox commandEdit;
    protected EditBox previousEdit;

    CommandSuggestions commandSuggestions;

    private final CommandBlockEntity autoCommandBlock;
    private CycleButton<CommandBlockEntity.Mode> modeButton;
    private CycleButton<Boolean> conditionalButton;
    private CycleButton<Boolean> autoexecButton;
    private CommandBlockEntity.Mode mode = CommandBlockEntity.Mode.REDSTONE;
    private boolean conditional;
    private boolean autoexec;

    public NoButtonCommandBlockScreen(CommandBlockEntity tileEntity) {
        super(NarratorChatListener.NO_TITLE);
        this.autoCommandBlock = tileEntity;
    }

    BaseCommandBlock getCommandBlock() {
        return this.autoCommandBlock.getCommandBlock();
    }

    int getPreviousY() {
        return 135;
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        boolean flag = this.getCommandBlock().isTrackOutput();
        this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20,
            new TranslatableComponent("advMode.command")) {
            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage()
                    .append(commandSuggestions.getNarrationMessage());
            }
        };
        this.commandEdit.setMaxLength(32500);
        this.commandEdit.setResponder(this::onEdited);
        this.addWidget(this.commandEdit);
        this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20,
            new TranslatableComponent("advMode.previousOutput"));
        this.previousEdit.setMaxLength(32500);
        this.previousEdit.setEditable(false);
        this.previousEdit.setValue("-");
        this.addWidget(this.previousEdit);
        this.setInitialFocus(this.commandEdit);
        this.commandEdit.setFocus(true);
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.commandEdit, this.font, true, true,
            0, 7, false, Integer.MIN_VALUE);
        this.commandSuggestions.setAllowSuggestions(true);
        this.commandSuggestions.updateCommandInfo();
        this.updatePreviousOutput(flag);

        this.modeButton = this.addRenderableWidget(CycleButton.<CommandBlockEntity.Mode>builder((p_169719_) -> {
            switch (p_169719_) {
                case SEQUENCE:
                    return new TranslatableComponent("advMode.mode.sequence");
                case AUTO:
                    return new TranslatableComponent("advMode.mode.auto");
                case REDSTONE:
                default:
                    return new TranslatableComponent("advMode.mode.redstone");
            }
        }).withValues(CommandBlockEntity.Mode.values()).displayOnlyValue().withInitialValue(this.mode).create(
            this.width / 2 - 50 - 100 - 4, 165, 100, 20, new TranslatableComponent("advMode.mode"),
            (p_169721_, p_169722_) -> {
                this.mode = p_169722_;
            }));
        this.conditionalButton = this.addRenderableWidget(CycleButton
            .booleanBuilder(new TranslatableComponent("advMode.mode.conditional"),
                new TranslatableComponent("advMode.mode.unconditional"))
            .displayOnlyValue().withInitialValue(this.conditional).create(this.width / 2 - 50, 165, 100, 20,
                new TranslatableComponent("advMode.type"), (p_169727_, p_169728_) -> {
                    this.conditional = p_169728_;
                }));
        this.autoexecButton = this.addRenderableWidget(CycleButton
            .booleanBuilder(new TranslatableComponent("advMode.mode.autoexec.bat"),
                new TranslatableComponent("advMode.mode.redstoneTriggered"))
            .displayOnlyValue().withInitialValue(this.autoexec).create(this.width / 2 + 50 + 4, 165, 100, 20,
                new TranslatableComponent("advMode.triggering"), (p_169724_, p_169725_) -> {
                    this.autoexec = p_169725_;
                }));

        this.enableControls(false);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void toggleOutput() {
        BaseCommandBlock basecommandblock = this.getCommandBlock();

        boolean output = !basecommandblock.isTrackOutput();
        basecommandblock.setTrackOutput(output);
        this.updatePreviousOutput(output);
    }

    private void enableControls(boolean p_169730_) {
        this.modeButton.active = p_169730_;
        this.conditionalButton.active = p_169730_;
        this.autoexecButton.active = p_169730_;
    }

    public void updateGui() {
        BaseCommandBlock basecommandblock = this.autoCommandBlock.getCommandBlock();
        this.commandEdit.setValue(basecommandblock.getCommand());
        boolean flag = basecommandblock.isTrackOutput();
        this.mode = this.autoCommandBlock.getMode();
        this.conditional = this.autoCommandBlock.isConditional();
        this.autoexec = this.autoCommandBlock.isAutomatic();
        this.modeButton.setValue(this.mode);
        this.conditionalButton.setValue(this.conditional);
        this.autoexecButton.setValue(this.autoexec);
        this.updatePreviousOutput(flag);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int length) {
        String s = this.commandEdit.getValue();
        this.init(minecraft, width, length);
        this.commandEdit.setValue(s);
        this.commandSuggestions.updateCommandInfo();
    }

    protected void populateAndSendPacket(BaseCommandBlock p_98384_) {
        this.minecraft.getConnection().send(new ServerboundSetCommandBlockPacket(new BlockPos(p_98384_.getPosition()),
            this.commandEdit.getValue(), this.mode, p_98384_.isTrackOutput(), this.conditional, this.autoexec));
    }

    @Override
    public void tick() {
        this.commandEdit.tick();
    }

    protected void updatePreviousOutput(boolean p_169599_) {
        this.previousEdit.setValue(p_169599_ ? this.getCommandBlock().getLastOutput().getString() : "-");
    }

    protected void onDone() {
        BaseCommandBlock basecommandblock = this.getCommandBlock();
        this.populateAndSendPacket(basecommandblock);
        if (!basecommandblock.isTrackOutput()) {
            basecommandblock.setLastOutput((Component) null);
        }

        this.minecraft.setScreen((Screen) null);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void onEdited(String p_97689_) {
        this.commandSuggestions.updateCommandInfo();
    }

    // TODO solution 2
//    @Override
//    public boolean charTyped(char character, int modifiers) {
//	long windowId = minecraft.getWindow().getWindow();
//
//	if (InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_ESCAPE)) {
//	    if (character == InputConstants.KEY_Q) {
//		this.onClose();
//		return true;
//	    } else if (character == InputConstants.KEY_X) {
//		this.onDone();
//		return true;
//	    }
//	} else if (InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_RIGHT_ALT)) {
//	    if (character == InputConstants.KEY_C) {
//		modeButton.onPress();
//		return true;
//	    } else if (character == InputConstants.KEY_A) {
//		conditionalButton.onPress();
//		return true;
//	    } else if (character == InputConstants.KEY_R) {
//		autoexecButton.onPress();
//		return true;
//	    } else if (character == InputConstants.KEY_O) {
//		toggleOutput();
//		return true;
//	    }
//	}
//
//	return super.charTyped(character, modifiers);
//    }

    // TODO solution 3 : Doesn't work
//    @Override
//    public boolean keyPressed(int keyCode, int x, int y) {
//	long windowId = minecraft.getWindow().getWindow();
//	Character character = KeyUtils.getCharacter(GLFW.glfwGetKeyScancode(keyCode));
//
//	System.out.println(character);
//
//	if (this.commandSuggestions.keyPressed(keyCode, x, y))
//	    return true;
//	else if (super.keyPressed(keyCode, x, y))
//	    return true;
//	else if (character != null) {
//	    if (InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_ESCAPE)) {
//		if (character == 'q') {
//		    this.onClose();
//		    return true;
//		} else if (keyCode == 'x') {
//		    this.onDone();
//		    return true;
//		} else
//		    return false;
//	    } else if (InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_RIGHT_ALT)) {
//		if (keyCode == 'c') {
//		    modeButton.onPress();
//		    return true;
//		} else if (keyCode == 'a') {
//		    conditionalButton.onPress();
//		    return true;
//		} else if (keyCode == 'r') {
//		    autoexecButton.onPress();
//		    return true;
//		} else if (keyCode == 'o') {
//		    toggleOutput();
//		    return true;
//		} else
//		    return false;
//	    } else
//		return false;
//	} else
//	    return false;
//    }

    @Override
    public boolean keyPressed(int keyCode, int x, int y) {
        long windowId = minecraft.getWindow().getWindow();

        if (this.commandSuggestions.keyPressed(keyCode, x, y))
            return true;
        else if (super.keyPressed(keyCode, x, y))
            return true;
        else {
            if (InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_ESCAPE)) {
                if (keyCode == GLFW.GLFW_KEY_Q) {
                    this.onClose();
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_X) {
                    this.onDone();
                    return true;
                } else
                    return false;
            } else if (InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_RIGHT_ALT)) {
                if (keyCode == GLFW.GLFW_KEY_C) {
                    modeButton.onPress();
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_A) {
                    conditionalButton.onPress();
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_R) {
                    autoexecButton.onPress();
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_O) {
                    toggleOutput();
                    return true;
                } else
                    return false;
            } else
                return false;
        }
    }

    @Override
    public boolean mouseScrolled(double p_97659_, double p_97660_, double p_97661_) {
        return this.commandSuggestions.mouseScrolled(p_97661_) ? true
            : super.mouseScrolled(p_97659_, p_97660_, p_97661_);
    }

    @Override
    public boolean mouseClicked(double p_97663_, double p_97664_, int p_97665_) {
        return this.commandSuggestions.mouseClicked(p_97663_, p_97664_, p_97665_) ? true
            : super.mouseClicked(p_97663_, p_97664_, p_97665_);
    }

    @Override
    public void render(PoseStack p_97672_, int p_97673_, int p_97674_, float p_97675_) {
        this.renderBackground(p_97672_);
        drawCenteredString(p_97672_, this.font, SET_COMMAND_LABEL, this.width / 2, 20, 16777215);
        drawString(p_97672_, this.font, COMMAND_LABEL, this.width / 2 - 150, 40, 10526880);
        this.commandEdit.render(p_97672_, p_97673_, p_97674_, p_97675_);
        int i = 75;
        if (!this.previousEdit.getValue().isEmpty()) {
            i += 5 * 9 + 1 + this.getPreviousY() - 135;
            drawString(p_97672_, this.font, PREVIOUS_OUTPUT_LABEL, this.width / 2 - 150, i + 4, 10526880);
            this.previousEdit.render(p_97672_, p_97673_, p_97674_, p_97675_);
        }

        super.render(p_97672_, p_97673_, p_97674_, p_97675_);
        this.commandSuggestions.render(p_97672_, p_97673_, p_97674_);
    }

}
