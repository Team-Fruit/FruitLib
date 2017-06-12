package net.teamfruit.fruitlib.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.Validate;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.teamfruit.fruitlib.loader.Log;
import net.teamfruit.lib.scheduler.BukkitScheduler;
import net.teamfruit.lib.scheduler.BukkitTask;
import net.teamfruit.lib.scheduler.BukkitWorker;

public class CraftScheduler implements BukkitScheduler {
	private final AtomicInteger ids = new AtomicInteger(1);

	private volatile CraftTask head = new CraftTask();

	private final AtomicReference<CraftTask> tail = new AtomicReference<CraftTask>(this.head);

	private final PriorityQueue<CraftTask> pending = new PriorityQueue<CraftTask>(10, new Comparator<CraftTask>() {
		@Override
		public int compare(final CraftTask o1, final CraftTask o2) {
			final int value = (int) (o1.getNextRun()-o2.getNextRun());

			return value!=0 ? value : o1.getTaskId()-o2.getTaskId();
		}
	});

	private final List<CraftTask> temp = new ArrayList<CraftTask>();

	private final ConcurrentHashMap<Integer, CraftTask> runners = new ConcurrentHashMap<Integer, CraftTask>();
	private volatile int currentTick = -1;
	private final Executor executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Fruit Scheduler Thread - %1$d").build());
	private CraftAsyncDebugger debugHead = new CraftAsyncDebugger(-1, null) {
		@Override
		StringBuilder debugTo(final StringBuilder string) {
			return string;
		}
	};
	private CraftAsyncDebugger debugTail = this.debugHead;

	private static final int RECENT_TICKS = 30;

	@Override
	public int scheduleSyncDelayedTask(final Runnable task) {
		return scheduleSyncDelayedTask(task, 0L);
	}

	@Override
	public BukkitTask runTask(final Runnable runnable) {
		return runTaskLater(runnable, 0L);
	}

	@Override
	public BukkitTask runTaskAsynchronously(final Runnable runnable) {
		return runTaskLaterAsynchronously(runnable, 0L);
	}

	@Override
	public int scheduleSyncDelayedTask(final Runnable task, final long delay) {
		return scheduleSyncRepeatingTask(task, delay, -1L);
	}

	@Override
	public BukkitTask runTaskLater(final Runnable runnable, final long delay) {
		return runTaskTimer(runnable, delay, -1L);
	}

	@Override
	public BukkitTask runTaskLaterAsynchronously(final Runnable runnable, final long delay) {
		return runTaskTimerAsynchronously(runnable, delay, -1L);
	}

	@Override
	public int scheduleSyncRepeatingTask(final Runnable runnable, final long delay, final long period) {
		return runTaskTimer(runnable, delay, period).getTaskId();
	}

	@Override
	public BukkitTask runTaskTimer(final Runnable runnable, long delay, long period) {
		validate(runnable);
		if (delay<0L)
			delay = 0L;
		if (period==0L)
			period = 1L;
		else if (period<-1L)
			period = -1L;
		return handle(new CraftTask(runnable, nextId(), period), delay);
	}

	@Override
	public BukkitTask runTaskTimerAsynchronously(final Runnable runnable, long delay, long period) {
		validate(runnable);
		if (delay<0L)
			delay = 0L;
		if (period==0L)
			period = 1L;
		else if (period<-1L)
			period = -1L;
		return handle(new CraftAsyncTask(this.runners, runnable, nextId(), period), delay);
	}

	@Override
	public <T> Future<T> callSyncMethod(final Callable<T> task) {
		validate(task);
		final CraftFuture<T> future = new CraftFuture<T>(task, nextId());
		handle(future, 0L);
		return future;
	}

	@Override
	public void cancelTask(final int taskId) {
		if (taskId<=0)
			return;
		CraftTask task = this.runners.get(taskId);
		if (task!=null)
			task.cancel0();
		task = new CraftTask(
				new Runnable() {
					@Override
					public void run() {
						if (!check(CraftScheduler.this.temp))
							check(CraftScheduler.this.pending);
					}

					private boolean check(final Iterable<CraftTask> collection) {
						final Iterator<CraftTask> tasks = collection.iterator();
						while (tasks.hasNext()) {
							final CraftTask task = tasks.next();
							if (task.getTaskId()==taskId) {
								task.cancel0();
								tasks.remove();
								if (task.isSync())
									CraftScheduler.this.runners.remove(taskId);
								return true;
							}
						}
						return false;
					}
				});
		handle(task, 0L);
		for (CraftTask taskPending = this.head.getNext(); taskPending!=null; taskPending = taskPending.getNext()) {
			if (taskPending==task)
				return;
			if (taskPending.getTaskId()==taskId)
				taskPending.cancel0();
		}
	}

	@Override
	public void cancelTasks() {
		Validate.notNull("Cannot cancel tasks of null plugin");
		final CraftTask task = new CraftTask(
				new Runnable() {
					@Override
					public void run() {
						check(CraftScheduler.this.pending);
						check(CraftScheduler.this.temp);
					}

					void check(final Iterable<CraftTask> collection) {
						final Iterator<CraftTask> tasks = collection.iterator();
						while (tasks.hasNext()) {
							final CraftTask task = tasks.next();
							// if (task.getOwner().equals(this.val$plugin)) {
							task.cancel0();
							tasks.remove();
							if (task.isSync())
								CraftScheduler.this.runners.remove(task.getTaskId());

							// }
						}
					}
				});
		handle(task, 0L);
		for (CraftTask taskPending = this.head.getNext(); taskPending!=null; taskPending = taskPending.getNext()) {
			if (taskPending==task)
				return;
			if (taskPending.getTaskId()!=-1/*&&taskPending.getOwner().equals(plugin)*/)
				taskPending.cancel0();
		}
		for (final CraftTask runner : this.runners.values())
			// if (runner.getOwner().equals(plugin))
			runner.cancel0();
	}

	@Override
	public void cancelAllTasks() {
		final CraftTask task = new CraftTask(
				new Runnable() {
					@Override
					public void run() {
						final Iterator<CraftTask> it = CraftScheduler.this.runners.values().iterator();
						while (it.hasNext()) {
							final CraftTask task = it.next();
							task.cancel0();
							if (task.isSync())
								it.remove();
						}
						CraftScheduler.this.pending.clear();
						CraftScheduler.this.temp.clear();
					}
				});
		handle(task, 0L);
		for (CraftTask taskPending = this.head.getNext(); taskPending!=null; taskPending = taskPending.getNext()) {
			if (taskPending==task)
				break;
			taskPending.cancel0();
		}
		for (final CraftTask runner : this.runners.values())
			runner.cancel0();
	}

	@Override
	public boolean isCurrentlyRunning(final int taskId) {
		final CraftTask task = this.runners.get(taskId);
		if (task==null||task.isSync())
			return false;
		final CraftAsyncTask asyncTask = (CraftAsyncTask) task;
		synchronized (asyncTask.getWorkers()) {
			return asyncTask.getWorkers().isEmpty();
		}
	}

	@Override
	public boolean isQueued(final int taskId) {
		if (taskId<=0)
			return false;
		for (CraftTask task = this.head.getNext(); task!=null; task = task.getNext())
			if (task.getTaskId()==taskId)
				return task.getPeriod()>=-1L;
		final CraftTask task = this.runners.get(taskId);
		return task!=null&&task.getPeriod()>=-1L;
	}

	@Override
	public List<BukkitWorker> getActiveWorkers() {
		final ArrayList<BukkitWorker> workers = new ArrayList<BukkitWorker>();
		for (final CraftTask taskObj : this.runners.values()) {
			if (taskObj.isSync())
				continue;
			final CraftAsyncTask task = (CraftAsyncTask) taskObj;
			synchronized (task.getWorkers()) {
				workers.addAll(task.getWorkers());
			}
		}
		return workers;
	}

	@Override
	public List<BukkitTask> getPendingTasks() {
		final ArrayList<CraftTask> truePending = new ArrayList<CraftTask>();
		for (CraftTask task = this.head.getNext(); task!=null; task = task.getNext()) {
			if (task.getTaskId()==-1)
				continue;
			truePending.add(task);

		}

		final ArrayList<BukkitTask> pending = new ArrayList<BukkitTask>();
		for (final CraftTask task : this.runners.values())
			if (task.getPeriod()>=-1L)
				pending.add(task);

		for (final CraftTask task : truePending)
			if (task.getPeriod()>=-1L&&!pending.contains(task))
				pending.add(task);
		return pending;
	}

	public void mainThreadHeartbeat(final int currentTick) {
		this.currentTick = currentTick;
		final List<CraftTask> temp = this.temp;
		parsePending();
		while (isReady(currentTick)) {
			final CraftTask task = this.pending.remove();
			if (task.getPeriod()<-1L) {
				if (task.isSync())
					this.runners.remove(task.getTaskId(), task);
				parsePending();
			} else {
				if (task.isSync()) {
					try {
						task.timings.startTiming();
						task.run();
						task.timings.stopTiming();
					} catch (final Throwable throwable) {
						Log.log.warn(String.format("Task #%s generated an exception", task.getTaskId()), throwable);
					}
					parsePending();
				} else {
					this.debugTail = this.debugTail.setNext(new CraftAsyncDebugger(currentTick+RECENT_TICKS, task.getTaskClass()));
					this.executor.execute(task);

				}

				final long period = task.getPeriod();
				if (period>0L) {
					task.setNextRun(currentTick+period);
					temp.add(task);
				} else if (task.isSync())
					this.runners.remove(task.getTaskId());
			}
		}
		this.pending.addAll(temp);
		temp.clear();
		this.debugHead = this.debugHead.getNextHead(currentTick);
	}

	private void addTask(final CraftTask task) {
		final AtomicReference<CraftTask> tail = this.tail;
		CraftTask tailTask = tail.get();
		while (!tail.compareAndSet(tailTask, task))
			tailTask = tail.get();
		tailTask.setNext(task);
	}

	private CraftTask handle(final CraftTask task, final long delay) {
		task.setNextRun(this.currentTick+delay);
		addTask(task);
		return task;
	}

	private static void validate(final Object task) {
		Validate.notNull(task, "Task cannot be null");
	}

	private int nextId() {
		return this.ids.incrementAndGet();
	}

	private void parsePending() {
		CraftTask head = this.head;
		CraftTask task = head.getNext();
		CraftTask lastTask = head;
		for (; task!=null; task = (lastTask = task).getNext())
			if (task.getTaskId()==-1)
				task.run();
			else if (task.getPeriod()>=-1L) {
				this.pending.add(task);
				this.runners.put(task.getTaskId(), task);
			}

		for (task = head; task!=lastTask; task = head) {
			head = task.getNext();
			task.setNext(null);
		}
		this.head = lastTask;
	}

	private boolean isReady(final int currentTick) {
		return !this.pending.isEmpty()&&this.pending.peek().getNextRun()<=currentTick;
	}

	@Override
	public String toString() {
		final int debugTick = this.currentTick;
		final StringBuilder string = new StringBuilder("Recent tasks from ").append(debugTick-RECENT_TICKS).append('-').append(debugTick).append('{');
		this.debugHead.debugTo(string);
		return string.append('}').toString();
	}
}
