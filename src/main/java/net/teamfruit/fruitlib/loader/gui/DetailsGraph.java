package net.teamfruit.fruitlib.loader.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import com.google.common.collect.Lists;

import net.teamfruit.fruitlib.loader.gui.Speed.CurrentSpeed;

public class DetailsGraph extends JComponent {
	private int objwidth = 2;
	private int objsplit = 4;
	private int objspace = 2;

	private int getObjunit() {
		return this.objwidth+this.objspace;
	}

	private boolean initialized;

	private Color textColor = Color.WHITE;
	private Color speedColor = Color.GRAY;
	private Color speedaveColor = Color.LIGHT_GRAY;

	private List<Integer> speed = Lists.newArrayList();
	private List<Integer> speedave = Lists.newArrayList();

	public int getObjWidth() {
		return this.objwidth;
	}

	public void setObjWidth(final int objwidth) {
		this.objwidth = objwidth;
	}

	public int getObjSplit() {
		return this.objsplit;
	}

	public void setObjSplit(final int objsplit) {
		this.objsplit = objsplit;
	}

	public int getObjSpace() {
		return this.objspace;
	}

	public void setObjSpace(final int objspace) {
		this.objspace = objspace;
	}

	public void setTextColor(final Color textColor) {
		this.textColor = textColor;
	}

	public Color getTextColor() {
		return this.textColor;
	}

	public void setSpeedColor(final Color speedColor) {
		this.speedColor = speedColor;
	}

	public Color getSpeedColor() {
		return this.speedColor;
	}

	public void setSpeedAverageColor(final Color speedaveColor) {
		this.speedaveColor = speedaveColor;
	}

	public Color getSpeedAverageColor() {
		return this.speedaveColor;
	}

	public void init() {
		this.initialized = true;
	}

	public List<Integer> getShownSpeed() {
		final int width = getWidth();
		final int size = width/(this.objwidth+this.objspace)+1;

		final List<Integer> shownspeed = this.speed.subList(Math.max(0, this.speed.size()-size), this.speed.size());
		return shownspeed;
	}

	public List<Integer> getShownAverageSpeed() {
		final int width = getWidth();
		final int size = width/(this.objwidth+this.objspace)+1;

		final List<Integer> shownspeedave = this.speedave.subList(Math.max(0, this.speedave.size()-size), this.speedave.size());
		return shownspeedave;
	}

	@Override
	public void paintComponent(final Graphics gs) {
		super.paintComponent(gs);

		if (!this.initialized) {
			final int width = getWidth();
			final int height = getHeight();
			gs.setColor(new Color(0x000000));
			gs.fillRect(0, 0, width/2, height/2);
			gs.fillRect(width/2, height/2, width, height);
			gs.setColor(new Color(0xff00dd));
			gs.fillRect(width/2, 0, width, height/2);
			gs.fillRect(0, height/2, width/2, height);
			return;
		}

		final Graphics2D g = (Graphics2D) gs;

		final int width = getWidth();
		final int height = getHeight();
		final int size = width/(this.objwidth+this.objspace)+1;

		final List<Integer> shownspeed = this.speed.subList(Math.max(0, this.speed.size()-size), this.speed.size());
		final List<Integer> shownspeedave = this.speedave.subList(Math.max(0, this.speedave.size()-size), this.speedave.size());

		if (!shownspeed.isEmpty()&&!shownspeedave.isEmpty()) {
			final int max = SizeUnit.SPEED.getMeasure(Math.max(Collections.max(shownspeed), Collections.max(shownspeedave)));

			final int objunit = getObjunit();

			// 棒グラフ
			final int shownspeed_all = shownspeed.size()*this.objsplit;
			int shownspeed_i = 0;
			for (final int shownspeed_one : shownspeed) {
				final int objlength = shownspeed_one*height/max;
				for (int r = 0; r<this.objsplit; r++)
					fillRect(g, width-objunit*(shownspeed_all-shownspeed_i++), height-objlength, this.objwidth, objlength, this.speedColor);
			}

			// 折れ線グラフ
			for (int ib = 1; ib<shownspeedave.size(); ib++) {
				final int ia = ib-1;

				final int ja = shownspeedave.get(ia);
				final int jb = shownspeedave.get(ib);

				if (ja<0||jb<0)
					continue;

				final int la = ja*height/max;
				final int lb = jb*height/max;

				final int a = width-objunit*(shownspeed.size()-ib)*this.objsplit-objunit;
				final int b = width-objunit*(shownspeed.size()-ib)*this.objsplit;
				final int c = width-objunit*(shownspeed.size()-ib)*this.objsplit+objunit*(this.objsplit-1);
				drawLine(g, a, height-la, b, height-lb, this.speedaveColor);
				drawLine(g, b, height-lb, c, height-lb, this.speedaveColor);
			}
		}
	}

	private void fillRect(final Graphics2D g, final int x, final int y, final int w, final int h, final Color c) {
		final float opacity = (float) Math.max(0d, Math.min(easing(x, 0d, 1d, 100), 1d));
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha()*opacity)));
		g.fillRect(x, y, w, h);
	}

	private void drawLine(final Graphics2D g, final int x1, final int y1, final int x2, final int y2, final Color c) {
		final float opacity = (float) Math.max(0d, Math.min(easing(x1, 0d, 1d, 100), 1d));
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha()*opacity)));
		g.drawLine(x1, y1, x2, y2);
	}

	private double easing(double t, final double b, final double c, final double d) {
		return c*(t /= d)*t*t*t+b;
	}

	public DetailsGraph addSpeed(final CurrentSpeed sp) {
		this.speed.add((int) sp.getSpeed());
		this.speedave.add((int) sp.getAverageSpeed());
		return this;
	}
}