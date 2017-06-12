package net.teamfruit.lib.event;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiScreen;

@Cancelable
public class GuiKeyInputEvent extends Event {
	public GuiScreen screen;
	private boolean mouseConsumed;
	private boolean keyboardConsumed;

	public void consumeInput() {
		consumeMouseInput();
		consumeKeyboardInput();
	}

	public void consumeMouseInput() {
		if (!this.mouseConsumed&&Mouse.isCreated()) {
			while (Mouse.next()) {
			}
			this.mouseConsumed = true;
		}
	}

	public void consumeKeyboardInput() {
		if (!this.keyboardConsumed&&Keyboard.isCreated()) {
			while (Keyboard.next()) {
			}
			this.keyboardConsumed = true;
		}
	}
}
