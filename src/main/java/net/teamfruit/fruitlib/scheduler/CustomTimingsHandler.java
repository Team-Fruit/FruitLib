package net.teamfruit.fruitlib.scheduler;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CustomTimingsHandler {
	private static Queue<CustomTimingsHandler> HANDLERS = new ConcurrentLinkedQueue<CustomTimingsHandler>();
	private final String name;
	private final CustomTimingsHandler parent;
	private long count;
	private long start;
	private long timingDepth;
	private long totalTime;
	private long curTickTotal;
	private long violations;

	public CustomTimingsHandler(final String name, final CustomTimingsHandler parent) {
		this.count = 0L;
		this.start = 0L;
		this.timingDepth = 0L;
		this.totalTime = 0L;
		this.curTickTotal = 0L;
		this.violations = 0L;
		this.name = name;
		this.parent = parent;
		HANDLERS.add(this);
	}

	public CustomTimingsHandler(final String name) {
		this(name, null);
	}

	public static void printTimings(final PrintStream printStream) {
		printStream.println("Minecraft");
		final Iterator<CustomTimingsHandler> livingEntities = HANDLERS.iterator();
		while (livingEntities.hasNext()) {
			final CustomTimingsHandler entities = livingEntities.next();
			final long time = entities.totalTime;
			final long count = entities.count;
			if (count!=0L) {

				final long avg = time/count;

				printStream.println("    "+entities.name+" Time: "+time+" Count: "+count+" Avg: "+avg+" Violations: "+entities.violations);
			}
		}
		/*		printStream.println("# Version "+Bukkit.getVersion());
				int entities1 = 0;
				int livingEntities1 = 0;
				World world;
				for (final Iterator arg8 = Bukkit.getWorlds().iterator(); arg8.hasNext(); livingEntities1 += world.getLivingEntities().size()) {
					world = (World) arg8.next();
					entities1 += world.getEntities().size();
				}
		
				printStream.println("# Entities "+entities1);
				printStream.println("# LivingEntities "+livingEntities1);*/
	}

	public static void reload() {
		final Iterator<CustomTimingsHandler> arg = HANDLERS.iterator();
		while (arg.hasNext()) {
			final CustomTimingsHandler timings = arg.next();
			timings.reset();
		}

		// TimingsCommand.timingStart = System.nanoTime();
	}

	public static void tick() {
		CustomTimingsHandler timings;
		for (final Iterator<CustomTimingsHandler> arg = HANDLERS.iterator(); arg.hasNext(); timings.timingDepth = 0L) {
			timings = arg.next();
			if (timings.curTickTotal>50000000L)

				timings.violations = (long) (timings.violations+Math.ceil(timings.curTickTotal/50000000L));

			timings.curTickTotal = 0L;
		}
	}

	public void startTiming() {
		if (++this.timingDepth==1L) {

			this.start = System.nanoTime();
			if (this.parent!=null&&++this.parent.timingDepth==1L)

				this.parent.start = this.start;
		}

	}

	public void stopTiming() {
		if (--this.timingDepth!=0L||this.start==0L)
			return;

		final long diff = System.nanoTime()-this.start;
		this.totalTime += diff;
		this.curTickTotal += diff;
		++this.count;
		this.start = 0L;
		if (this.parent!=null)
			this.parent.stopTiming();
	}

	public void reset() {
		this.count = 0L;
		this.violations = 0L;
		this.curTickTotal = 0L;
		this.totalTime = 0L;
		this.start = 0L;
		this.timingDepth = 0L;
	}
}