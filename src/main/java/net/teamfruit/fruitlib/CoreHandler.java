package net.teamfruit.fruitlib;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.teamfruit.fruitlib.scheduler.CraftScheduler;
import net.teamfruit.lib.FruitCommon;
import net.teamfruit.lib.Log;

public class CoreHandler {
	public static final CraftScheduler scheduler = new CraftScheduler();

	public void init() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		Config.getConfig().getBase().onConfigChanged(eventArgs);
	}

	@SubscribeEvent
	public void onDraw(final @Nonnull RenderGameOverlayEvent.Post event) {
	}

	@SubscribeEvent
	public void onTickClient(final @Nonnull ClientTickEvent event) {
		final String str = GuiScreen.getClipboardString();
		if (StringUtils.startsWith(str, "#!FRUIT=")) {

		}
		if (event.phase==Phase.END)
			debugKey();
	}

	@SubscribeEvent
	public void onTick(final @Nonnull TickEvent event) {
		if (event.side.isServer())
			scheduler.mainThreadHeartbeat(FruitCommon.server().getTickCounter());
		else
			;//scheduler.mainThreadHeartbeat(FruitClient.client().);

	}

	private boolean debugKey;

	private void debugKey() {
		if (Keyboard.isKeyDown(Keyboard.KEY_I)&&Keyboard.isKeyDown(Keyboard.KEY_O)&&Keyboard.isKeyDown(Keyboard.KEY_P)) {
			if (!this.debugKey)
				debug();
			this.debugKey = true;
		} else
			this.debugKey = false;
	}

	private void debug() {
		Log.log.info("launch");
		try {
			MinecraftLauncher.launch();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
