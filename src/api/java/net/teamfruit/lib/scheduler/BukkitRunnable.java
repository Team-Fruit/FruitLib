package net.teamfruit.lib.scheduler;

import net.teamfruit.lib.FruitCommon;

public abstract class BukkitRunnable implements Runnable {
	private int taskId = -1;

	public synchronized void cancel() throws IllegalStateException {
		FruitCommon.getScheduler().cancelTask(getTaskId());
	}

	public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(FruitCommon.getScheduler().runTask(this));
	}

	public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(FruitCommon.getScheduler().runTaskAsynchronously(this));
	}

	public synchronized BukkitTask runTaskLater(final long delay) throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(FruitCommon.getScheduler().runTaskLater(this, delay));
	}

	public synchronized BukkitTask runTaskLaterAsynchronously(final long delay) throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(FruitCommon.getScheduler().runTaskLaterAsynchronously(this, delay));
	}

	public synchronized BukkitTask runTaskTimer(final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(FruitCommon.getScheduler().runTaskTimer(this, delay, period));
	}

	public synchronized BukkitTask runTaskTimerAsynchronously(final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(FruitCommon.getScheduler().runTaskTimerAsynchronously(this, delay, period));
	}

	public synchronized int getTaskId() throws IllegalStateException {
		final int id = this.taskId;
		if (id==-1)
			throw new IllegalStateException("Not scheduled yet");
		else
			return id;
	}

	private void checkState() {
		if (this.taskId!=-1)
			throw new IllegalStateException("Already scheduled as "+this.taskId);
	}

	private BukkitTask setupId(final BukkitTask task) {
		this.taskId = task.getTaskId();
		return task;
	}
}