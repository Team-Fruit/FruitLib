package net.teamfruit.lib.scheduler;

public interface BukkitTask {
	int getTaskId();

	boolean isSync();

	void cancel();
}