package net.teamfruit.fruitlib.loader.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class FitImage extends JComponent {
	private Image image;

	public FitImage() {
	}

	public void setImage(final Image image) {
		this.image = image;
	}

	public Image getImage() {
		return this.image;
	}

	private Dimension size(final int w, final int h, int maxw, int maxh) {
		if (w<0)
			maxw *= -1;
		if (h<0)
			maxh *= -1;
		final boolean b = w/maxw>h/maxh;
		return new Dimension(b ? maxw : w*maxh/h, b ? h*maxw/w : maxh);
	}

	@Override
	public Dimension getPreferredSize() {
		final Image img = getImage();
		if (img!=null) {
			final int w = img.getWidth(this);
			final int h = img.getHeight(this);
			if (w>0&&h>0)
				return new Dimension(w, h);
		}
		return super.getPreferredSize();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();
		super.paintComponent(g);
		final Image img = getImage();
		if (img!=null) {
			final int wid = getWidth();
			final int hei = getHeight();
			int w = img.getWidth(this);
			if (w<=0)
				w = wid;
			int h = img.getHeight(this);
			if (h<=0)
				h = hei;
			final Dimension d = size(w, h, wid, hei);
			g.drawImage(this.image, (wid-d.width)/2, (hei-d.height)/2, d.width, d.height, this);
		} else {
			g.setColor(new Color(0x000000));
			g.fillRect(0, 0, width/2, height/2);
			g.fillRect(width/2, height/2, width, height);
			g.setColor(new Color(0xff00dd));
			g.fillRect(width/2, 0, width, height/2);
			g.fillRect(0, height/2, width/2, height);
		}
	}
}
