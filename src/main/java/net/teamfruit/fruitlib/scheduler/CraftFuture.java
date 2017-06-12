package net.teamfruit.fruitlib.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class CraftFuture<T> extends CraftTask implements Future<T> {
	private final Callable<T> callable;
	private T value;
	private Exception exception = null;

	CraftFuture(final Callable<T> callable, final int id) {
		super((Runnable) null, id, -1L);
		this.callable = callable;
	}

	@Override
	public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
		if (getPeriod()!=-1L)
			return false;
		else {
			setPeriod(-2L);
			return true;
		}
	}

	@Override
	public boolean isCancelled() {
		return getPeriod()==-2L;
	}

	@Override
	public boolean isDone() {
		final long period = getPeriod();
		return period!=-1L&&period!=-3L;
	}

	@Override
	public T get() throws CancellationException, InterruptedException, ExecutionException {
		try {
			return this.get(0L, TimeUnit.MILLISECONDS);
		} catch (final TimeoutException arg1) {
			throw new Error(arg1);
		}
	}

	@Override
	public synchronized T get(long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		timeout = unit.toMillis(timeout);
		long period = getPeriod();
		long timestamp = timeout>0L ? System.currentTimeMillis() : 0L;

		while (period==-1L||period==-3L) {
			this.wait(timeout);
			period = getPeriod();
			if (period!=-1L&&period!=-3L)
				break;
			if (timeout!=0L) {

				timeout += timestamp-(timestamp = System.currentTimeMillis());
				if (timeout<=0L)
					throw new TimeoutException();
			}
		}

		if (period==-2L)
			throw new CancellationException();
		else if (period==-4L) {
			if (this.exception==null)
				return this.value;
			else
				throw new ExecutionException(this.exception);
		} else
			throw new IllegalStateException("Expected -1 to -4, got "+period);
	}

	@Override
	public void run() {
		synchronized (this) {
			if (getPeriod()==-2L)
				return;
			setPeriod(-3L);
		}
		try {
			this.value = this.callable.call();
		} catch (final Exception arg10) {
			this.exception = arg10;
		} finally {
			synchronized (this) {
				setPeriod(-4L);
				notifyAll();
			}
		}

	}

	@Override
	synchronized boolean cancel0() {
		if (getPeriod()!=-1L)
			return false;
		else {
			setPeriod(-2L);
			notifyAll();
			return true;
		}
	}
}