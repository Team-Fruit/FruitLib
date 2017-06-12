package net.teamfruit.fruitlib.scheduler;

import java.util.HashMap;

import net.teamfruit.lib.scheduler.BukkitTask;

public class SpigotTimings {
	public static final CustomTimingsHandler schedulerSyncTimer;

	public static final HashMap<String, CustomTimingsHandler> pluginTaskTimingMap;

	static {
		schedulerSyncTimer = new CustomTimingsHandler("** Scheduler - Sync Tasks");

		pluginTaskTimingMap = new HashMap<String, CustomTimingsHandler>();
	}

	public static CustomTimingsHandler getPluginTaskTimings(final BukkitTask task, final long period) {
		if (!task.isSync())
			return null;
		else {
			final CraftTask ctask = (CraftTask) task;
			final String taskname = ctask.getTaskName();

			String name = "Runnable: "+taskname;
			if (period>0L)
				name = name+"(interval:"+period+")";
			else
				name = name+"(Single)";
			CustomTimingsHandler result = pluginTaskTimingMap.get(name);
			if (result==null) {
				result = new CustomTimingsHandler(name, schedulerSyncTimer);
				pluginTaskTimingMap.put(name, result);
			}
			return result;
		}
	}
}