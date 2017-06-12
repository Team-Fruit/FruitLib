package net.teamfruit.fruitlib.loader.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;

import net.teamfruit.fruitlib.loader.Log;

public class LoadingSprite extends JComponent {
	private final List<LoaderAtlas> atlas = Lists.newArrayList();
	private int frameNum = 0;
	private boolean initialized;

	private Timer timer = new Timer(20, new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			updateFrame();
			repaint();
			LoadingSprite.this.timer.start();
		}
	});

	public LoadingSprite() {
		setPreferredSize(new Dimension(128, 128));
	}

	public LoadingSprite load() {
		if (!this.initialized) {
			final URL url1 = this.getClass().getClassLoader().getResource("assets/fruitlib/textures/sprite.png");
			final URL url2 = this.getClass().getClassLoader().getResource("assets/fruitlib/textures/sprite.txt");
			BufferedImage sprite1 = null;
			List<String> sprite2 = null;
			InputStream input1 = null;
			InputStream input2 = null;
			try {
				sprite1 = ImageIO.read(input1 = url1.openStream());
				sprite2 = IOUtils.readLines(input2 = url2.openStream());
			} catch (final IOException e) {
				Log.log.warn(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(input1);
				IOUtils.closeQuietly(input2);
			}
			this.atlas.addAll(LoaderAtlas.fromMeta(sprite1, sprite2));

			final LoaderAtlas f = this.atlas.get(0);
			setPreferredSize(new Dimension(f.width, f.height));
			this.timer.start();
			this.initialized = true;
		}
		return this;
	}

	@Override
	public void paintComponent(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();
		super.paintComponent(g);
		if (this.atlas.size()>0) {
			final LoaderAtlas at = this.atlas.get(this.frameNum);
			g.drawImage(at.image, 0, 0, width, height, at.x, at.y, at.x+at.width, at.y+at.height, this);
		} else {
			g.setColor(new Color(0x000000));
			g.fillRect(0, 0, width/2, height/2);
			g.fillRect(width/2, height/2, width, height);
			g.setColor(new Color(0xff00dd));
			g.fillRect(width/2, 0, width, height/2);
			g.fillRect(0, height/2, width/2, height);
		}
	}

	private void updateFrame() {
		final int size = this.atlas.size();
		if (size>0)
			this.frameNum = (this.frameNum+1)%size;
	}

	public static class LoaderAtlas {
		public final BufferedImage image;
		public final int x;
		public final int y;
		public final int width;
		public final int height;

		public LoaderAtlas(final BufferedImage image, final int x, final int y, final int width, final int height) {
			this.image = image;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public static List<LoaderAtlas> fromTextureCompressed(final BufferedImage image, final int atlasWidth, final int atlasHeight) {
			final List<LoaderAtlas> atlas = Lists.newArrayList();
			final int width = image.getWidth();
			final int height = image.getHeight();
			boolean vertical = false;
			int sx = 0, sy = 0;
			while (sx<width&&sy<height) {
				if (!vertical) {
					for (int ix = sx; ix<width; ix += atlasWidth)
						atlas.add(new LoaderAtlas(image, ix, sy, atlasWidth, atlasHeight));
					sy += atlasHeight;
				} else {
					for (int iy = sy; iy<height; iy += atlasHeight)
						atlas.add(new LoaderAtlas(image, sx, iy, atlasWidth, atlasHeight));
					sx += atlasWidth;
				}
				vertical = !vertical;
			}
			return atlas;
		}

		public static List<LoaderAtlas> fromTexture(final BufferedImage image, final int atlasWidth, final int atlasHeight) {
			final List<LoaderAtlas> atlas = Lists.newArrayList();
			final int width = image.getWidth();
			final int height = image.getHeight();
			for (int iy = 0; iy<height; iy += atlasHeight)
				for (int ix = 0; ix<width; ix += atlasWidth)
					atlas.add(new LoaderAtlas(image, ix, iy, atlasWidth, atlasHeight));
			return atlas;
		}

		public static List<LoaderAtlas> fromMeta(final BufferedImage image, final List<String> metas) {
			final List<LoaderAtlas> atlas = Lists.newArrayList();
			LoaderAtlas last = null;
			for (final String meta0 : metas) {
				final String[] meta1 = meta0.split(" ");
				if (meta1.length<4) {
					if (meta1.length==1&&last!=null) {
						final int amp = NumberUtils.toInt(meta1[0]);
						for (int i = 0; i<amp; i++)
							atlas.add(last);
					}
					continue;
				}
				final int x = NumberUtils.toInt(meta1[0]);
				final int y = NumberUtils.toInt(meta1[1]);
				final int w = NumberUtils.toInt(meta1[2]);
				final int h = NumberUtils.toInt(meta1[3]);
				atlas.add(last = new LoaderAtlas(image, x, y, w, h));
			}
			return atlas;
		}
	}
}
