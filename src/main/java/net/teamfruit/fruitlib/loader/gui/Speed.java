package net.teamfruit.fruitlib.loader.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import com.google.common.collect.EvictingQueue;

/**
 * ダウンロードの速度を計算します。
 * @author Kamesuta
 */
public class Speed {
	/**
	 * ナノ秒と秒の比率
	 */
	public static final double NANOS_PER_SECOND = 1000000000.0;
	/**
	 * ビットとバイトの比率
	 */
	public static final int BIT_PER_BYTE = 8;

	private long oldTime;
	private final int averagesize;

	private int lastcount;
	private int lasttime;

	private Queue<Integer> aveCount;
	private Queue<Integer> aveTime;

	/**
	 * 平均の蓄積量を指定してください。
	 * @param averagesize 蓄積量
	 */
	public Speed(final int averagesize) {
		this.averagesize = averagesize;
		this.aveCount = EvictingQueue.create(averagesize);
		this.aveTime = EvictingQueue.create(averagesize);
	}

	/**
	 * 情報を更新します
	 * @param count 前回との差
	 */
	public Speed update(final int count) {
		// get current time
		final long newTime = System.nanoTime();

		final int lastTime = (int) (newTime-this.oldTime);

		// update count
		this.aveCount.add(this.lastcount = count);
		this.aveTime.add(this.lasttime = lastTime);

		// reset
		this.oldTime = newTime;

		return this;
	}

	public CurrentSpeed getSample() {
		return new CurrentSpeed(this.lastcount, this.lasttime,
				this.aveCount.size()>this.averagesize/4 ? average(this.aveCount) : -1,
				this.aveTime.size()>this.averagesize/4 ? average(this.aveTime) : -1,
				Collections.max(this.aveCount), Collections.max(this.aveTime));
	}

	/**
	 * 平均を求めます
	 * @param list リスト
	 * @return 平均
	 */
	public static double average(final Collection<? extends Number> list) {
		double d = 0d;
		for (final Number n : list)
			d += n.doubleValue();
		return d/list.size();
	}

	public static class CurrentSpeed {
		private int lastCount;
		private int lastTime;
		private double aveCount;
		private double aveTime;
		private int maxCount;
		private int maxTime;

		public CurrentSpeed(final int lastcount, final int lasttime, final double avecount, final double avetime, final int maxcount, final int maxtime) {
			this.lastCount = lastcount;
			this.lastTime = lasttime;
			this.aveCount = avecount;
			this.aveTime = avetime;
			this.maxCount = maxcount;
			this.maxTime = maxtime;
		}

		/**
		 * update間の回数を返す
		 */
		public int getCount() {
			return this.lastCount;
		}

		/**
		 * update間の平均回数を返す
		 */
		public double getAverageCount() {
			return this.aveCount;
		}

		/**
		 * update間の最大回数を返す
		 */
		public int getMaxCount() {
			return this.maxCount;
		}

		/**
		 * update間の時間を返す
		 */
		public int getTime() {
			return this.lastTime;
		}

		/**
		 * update間の平均時間を返す
		 */
		public double getAverageTime() {
			return this.aveTime;
		}

		/**
		 * update間の最大時間を返す
		 */
		public int getMaxTime() {
			return this.maxTime;
		}

		/**
		 * 速度を返す
		 * @return byte/seconds
		 */
		public double getByteSpeed() {
			if (this.lastTime>0&&this.lastCount>0)
				return NANOS_PER_SECOND*this.lastCount/this.lastTime;
			else
				return -1;
		}

		/**
		 * 平均速度を返す
		 * @return byte/seconds
		 */
		public double getAverageByteSpeed() {
			final double timeave = this.aveTime;
			final double countave = this.aveCount;
			if (timeave>0&&countave>0)
				return NANOS_PER_SECOND*countave/timeave;
			else
				return -1;
		}

		/**
		 * 最大速度を返す
		 * @return byte/seconds
		 */
		public double getMaxByteSpeed() {
			final double timemax = this.maxTime;
			final double countmax = this.maxCount;
			if (timemax>0&&countmax>0)
				return NANOS_PER_SECOND*countmax/timemax;
			else
				return -1;
		}

		/**
		 * 速度を返す
		 * @return bit/seconds
		 */
		public double getSpeed() {
			return getByteSpeed()*BIT_PER_BYTE;
		}

		/**
		 * 平均速度を返す
		 * @return bit/seconds
		 */
		public double getAverageSpeed() {
			return getAverageByteSpeed()*BIT_PER_BYTE;
		}

		/**
		 * 最大速度を返す
		 * @return bit/seconds
		 */
		public double getMaxSpeed() {
			return getMaxByteSpeed()*BIT_PER_BYTE;
		}

		/**
		 * 推定残り時間を返す
		 * @return seconds
		 */
		public int getFull(final long bytes) {
			final double speed = getByteSpeed();
			if (speed>0)
				return (int) (bytes/speed);
			else
				return -1;
		}

		/**
		 * 推定残り時間を返す
		 * @return seconds
		 */
		public int getAverageFull(final long bytes) {
			final double speed = getAverageByteSpeed();
			if (speed>0)
				return (int) (bytes/speed);
			else
				return -1;
		}
	}
}