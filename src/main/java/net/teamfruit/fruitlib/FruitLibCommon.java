package net.teamfruit.fruitlib;

import net.teamfruit.lib.IFruitCommon;
import net.teamfruit.lib.scheduler.BukkitScheduler;

public class FruitLibCommon implements IFruitCommon {
	@Override
	public BukkitScheduler getScheduler() {
		return CoreHandler.scheduler;
	}
}
