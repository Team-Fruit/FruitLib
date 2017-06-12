package net.teamfruit.fruitlib.scheduler;

import net.teamfruit.lib.FruitCommon;
import net.teamfruit.lib.scheduler.BukkitTask;

public class CraftTask implements BukkitTask, Runnable {
	private volatile CraftTask next;
	private volatile long period;
	private long nextRun;
	private final Runnable task;
	private final int id;
	final CustomTimingsHandler timings;
	public String timingName;

	CraftTask(final String timingName, final Runnable task, final int id, final long period) {
		this.next = null;
		this.task = task;
		this.id = id;
		this.period = period;
		this.timingName = timingName==null&&task==null ? "Unknown" : timingName;
		this.timings = isSync() ? SpigotTimings.getPluginTaskTimings(this, period) : null;
	}

	CraftTask() {
		this((Runnable) null, -1, -1L);
	}

	CraftTask(final Runnable task) {
		this(task, -1, -1L);
	}

	CraftTask(final String timingName) {
		this(timingName, (Runnable) null, -1, -1L);
	}

	CraftTask(final String timingName, final Runnable task) {
		this(timingName, task, -1, -1L);
	}

	CraftTask(final Runnable task, final int id, final long period) {
		this((String) null, task, id, period);
	}

	@Override
	public final int getTaskId() {
		return this.id;
	}

	@Override
	public boolean isSync() {
		return true;
	}

	@Override
	public void run() {
		this.task.run();
	}

	long getPeriod() {
		return this.period;
	}

	void setPeriod(final long period) {
		this.period = period;
	}

	long getNextRun() {
		return this.nextRun;
	}

	void setNextRun(final long nextRun) {
		this.nextRun = nextRun;
	}

	CraftTask getNext() {
		return this.next;
	}

	void setNext(final CraftTask next) {
		this.next = next;
	}

	Class<? extends Runnable> getTaskClass() {
		return this.task.getClass();
	}

	@Override
	public void cancel() {
		FruitCommon.getScheduler().cancelTask(this.id);
	}

	boolean cancel0() {
		setPeriod(-2L);
		return true;
	}

	public String getTaskName() {
		return this.timingName!=null ? this.timingName : this.task.getClass().getName();
	}
}