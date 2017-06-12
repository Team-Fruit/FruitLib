package net.teamfruit.lib;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.teamfruit.lib.scheduler.BukkitScheduler;

public class FruitCommon {
	private static IFruitCommon fruit;

	public static void setFruit(final IFruitCommon fruitLibCommon) {
		if (fruit!=null)
			throw new UnsupportedOperationException("Cannot redefine singleton Fruit instance");
		else
			fruit = fruitLibCommon;
	}

	public static IFruitCommon getFruit() {
		return fruit;
	}

	private static MinecraftServer server;

	public static MinecraftServer server() {
		if (server==null)
			server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server;
	}

	public static BukkitScheduler getScheduler() {
		return fruit.getScheduler();
	}
}
