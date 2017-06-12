package net.teamfruit.fruitlib;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiScreen;
import net.teamfruit.lib.CoreInvoke;
import net.teamfruit.lib.event.Events;
import net.teamfruit.lib.event.GuiKeyInputEvent;

public class FruitLibHooks {
	@CoreInvoke
	public static boolean onGuiKeyInput(final @Nonnull GuiScreen screen) {
		final GuiKeyInputEvent event = new GuiKeyInputEvent();
		event.screen = screen;
		Events.post(event);
		return event.isCanceled();
	}
}
