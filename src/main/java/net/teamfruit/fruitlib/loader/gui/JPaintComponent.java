package net.teamfruit.fruitlib.loader.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;

public class JPaintComponent extends JPanel {
	protected Paint[] paints;

	public JPaintComponent() {
	}

	protected Paint[] getPaints() {
		return this.paints;
	}

	public JPaintComponent setPaints(final Paint... paint) {
		this.paints = paint;
		return this;
	}

	@Override
	public void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);
		final Graphics2D g = (Graphics2D) graphics;
		final Paint[] paints = getPaints();
		if (paints!=null)
			for (final Paint paint : paints) {
				g.setPaint(paint);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
	}
}