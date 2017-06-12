package net.teamfruit.fruitlib;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.teamfruit.fruitlib.command.FruitCommands;

/**
 * 共通処理
 *
 * @author Kamesuta
 */
public class CommonProxy {
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		Config.init(event.getSuggestedConfigurationFile(), "1.0.0");
	}

	public void init(final @Nonnull FMLInitializationEvent event) {
		new CoreHandler().init();
	}

	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		Config.getConfig().getBase().save();
	}

	public void serverStarting(final @Nonnull FMLServerStartingEvent event) {
		event.registerServerCommand(FruitCommands.instance);
	}
}