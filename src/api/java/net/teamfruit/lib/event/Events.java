package net.teamfruit.lib.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;

public class Events {
	public static <T extends Event> T register(final T event) {
		FMLCommonHandler.instance().bus().register(event);
		MinecraftForge.EVENT_BUS.register(event);
		return event;
	}

	public static <T extends Event> T post(final T event) {
		FMLCommonHandler.instance().bus().post(event);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}
}
