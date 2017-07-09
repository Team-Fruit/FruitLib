package net.teamfruit.fruitlib.loader.gui;

/**
 * ファイルのサイズについて扱います
 * @author Kamesuta
 *
 */
public enum SizeUnit {
	/**
	 * 保存領域のサイズを計算します。
	 */
	STORAGE(1024, 2, new String[] { "bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" }),
	/**
	 * 速度などのサイズを計算します。
	 */
	SPEED(1024, 2, new String[] { "bps", "kbps", "Mbps", "Gbps", "Tbps", "Pbps", "Ebps", "Zbps", "Ybps" });

	private final int pow;
	private final int bottom;
	private final String[] suffix;

	private SizeUnit(final int pow, final int bottom, final String[] suffix) {
		this.pow = pow;
		this.bottom = bottom;
		this.suffix = suffix;
	}

	/**
	 * サイズを単位を付けてフォーマットします。
	 * @param size サイズ
	 * @param digit 小数桁数
	 * @return フォーマット済み文字列
	 */
	public String getFormatSizeString(double size, final int digit) {
		int index = 0;

		while (size>=this.pow) {
			size /= this.pow;
			index++;
			if (index+1>=this.suffix.length)
				break;
		}

		return String.format("%."+Integer.toString(digit)+"f %s", size, index<this.suffix.length ? this.suffix[index] : "-");
	}

	/**
	 * サイズに適した最大値を返します
	 * @param size サイズ
	 * @return 単位の最大値
	 */
	public int getMeasure(float size) {
		int index = 0;

		while (size>=this.bottom) {
			size /= this.bottom;
			index++;
		}

		return (int) Math.pow(this.bottom, index+1);
	}
}