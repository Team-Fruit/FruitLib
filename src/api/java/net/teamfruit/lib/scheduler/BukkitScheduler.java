package net.teamfruit.lib.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface BukkitScheduler {
	int scheduleSyncDelayedTask(Runnable arg1, long arg2);

	int scheduleSyncDelayedTask(Runnable arg1);

	int scheduleSyncRepeatingTask(Runnable arg1, long arg2, long arg4);

	<T> Future<T> callSyncMethod(Callable<T> arg1);

	void cancelTask(int arg0);

	void cancelTasks();

	void cancelAllTasks();

	boolean isCurrentlyRunning(int arg0);

	boolean isQueued(int arg0);

	List<BukkitWorker> getActiveWorkers();

	List<BukkitTask> getPendingTasks();

	BukkitTask runTask(Runnable arg1) throws IllegalArgumentException;

	BukkitTask runTaskAsynchronously(Runnable arg1) throws IllegalArgumentException;

	BukkitTask runTaskLater(Runnable arg1, long arg2) throws IllegalArgumentException;

	BukkitTask runTaskLaterAsynchronously(Runnable arg1, long arg2) throws IllegalArgumentException;

	BukkitTask runTaskTimer(Runnable arg1, long arg2, long arg4) throws IllegalArgumentException;

	BukkitTask runTaskTimerAsynchronously(Runnable arg1, long arg2, long arg4) throws IllegalArgumentException;
}