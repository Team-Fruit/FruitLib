package net.teamfruit.fruitlib.scheduler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.teamfruit.fruitlib.loader.Log;
import net.teamfruit.lib.scheduler.BukkitWorker;

class CraftAsyncTask extends CraftTask {
	private final LinkedList<BukkitWorker> workers = new LinkedList<BukkitWorker>();
	private final Map<Integer, CraftTask> runners;

	CraftAsyncTask(final Map<Integer, CraftTask> runners, final Runnable task, final int id, final long delay) {
		super(task, id, delay);
		this.runners = runners;
	}

	@Override
	public boolean isSync() {
		return false;
	}

	@Override
	public void run() {
		final Thread thread = Thread.currentThread();
		synchronized (this.workers) {
			if (getPeriod()==-2L)
				return;
			this.workers.add(new BukkitWorker() {
				@Override
				public Thread getThread() {
					return thread;
				}

				@Override
				public int getTaskId() {
					return CraftAsyncTask.this.getTaskId();
				}
			});
		}

		Throwable thrown1 = null;
		try {
			super.run();
		} catch (final Throwable arg44) {
			thrown1 = arg44;
			Log.log.warn(String.format("an exception while executing task %s", getTaskId()), arg44);
		} finally {
			synchronized (this.workers) {
				try {
					final Iterator<BukkitWorker> workers = this.workers.iterator();
					boolean removed = false;
					while (workers.hasNext())
						if (workers.next().getThread()==thread) {
							workers.remove();
							removed = true;
							break;
						}

					if (!removed)
						throw new IllegalStateException(String.format("Unable to remove worker %s on task %s", thread.getName(), Integer.valueOf(getTaskId())), thrown1);
				} finally {
					if (getPeriod()<0L&&this.workers.isEmpty())
						this.runners.remove(Integer.valueOf(getTaskId()));
				}
			}
		}

	}

	LinkedList<BukkitWorker> getWorkers() {
		return this.workers;
	}

	@Override
	boolean cancel0() {
		synchronized (this.workers) {
			setPeriod(-2L);
			if (this.workers.isEmpty())
				this.runners.remove(Integer.valueOf(getTaskId()));
			return true;
		}
	}
}