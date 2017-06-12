package net.teamfruit.lib.scheduler;

public interface BukkitWorker {
	int getTaskId();

	Thread getThread();
}