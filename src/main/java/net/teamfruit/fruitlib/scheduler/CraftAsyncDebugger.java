package net.teamfruit.fruitlib.scheduler;

class CraftAsyncDebugger {
	private CraftAsyncDebugger next = null;
	private final int expiry;
	private final Class<? extends Runnable> clazz;

	CraftAsyncDebugger(final int expiry, final Class<? extends Runnable> clazz) {
		this.expiry = expiry;
		this.clazz = clazz;

	}

	final CraftAsyncDebugger getNextHead(final int time) {
		CraftAsyncDebugger current;
		CraftAsyncDebugger next;
		for (current = this; time>current.expiry; current = next) {
			next = current.next;
			if (current.next==null)
				break;
		}

		return current;
	}

	final CraftAsyncDebugger setNext(final CraftAsyncDebugger next) {
		return this.next = next;
	}

	StringBuilder debugTo(final StringBuilder string) {
		for (CraftAsyncDebugger next = this; next!=null; next = next.next)
			string.append(next.clazz.getName()).append('@').append(next.expiry).append(',');
		return string;
	}
}