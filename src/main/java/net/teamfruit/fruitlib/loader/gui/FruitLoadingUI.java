package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FruitLoadingUI {

	private final LoaderUIController controller = new LoaderUIController();

	public LoaderUIController getController() {
		return this.controller;
	}

	private JFrame frmFruitloaderDownloading;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final FruitLoadingUI window = new FruitLoadingUI();
					window.frmFruitloaderDownloading.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FruitLoadingUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frmFruitloaderDownloading = new JFrame();
		this.frmFruitloaderDownloading.setIconImage(Toolkit.getDefaultToolkit().getImage(FruitLoadingUI.class.getResource("/assets/fruitlib/textures/icon.png")));
		this.frmFruitloaderDownloading.setTitle("FruitLoader - Downloading");
		this.frmFruitloaderDownloading.setSize(750, 500);
		this.frmFruitloaderDownloading.setMinimumSize(new Dimension(750, 500));
		this.frmFruitloaderDownloading.setLocationRelativeTo(null);
		this.frmFruitloaderDownloading.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel application = new JPanel();
		this.frmFruitloaderDownloading.getContentPane().add(application, BorderLayout.CENTER);
		application.setLayout(new BorderLayout(0, 0));

		final LoadingProgress progress = new LoadingProgress(this.controller);
		application.add(progress, BorderLayout.NORTH);

		final JPanel contents = new JPanel();
		contents.setBackground(new Color(0x161616));
		application.add(contents, BorderLayout.CENTER);
		contents.setLayout(new LayeredLayout());

		final LoadingContents scrollcontent = new LoadingContents(this.controller);
		scrollcontent.setOpaque(false);

		final JScrollPane scrollpane = new JScrollPane(scrollcontent);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBorder(null);
		scrollpane.setOpaque(false);
		scrollpane.getViewport().setOpaque(false);
		final JScrollBar bar = scrollpane.getVerticalScrollBar();
		bar.setUnitIncrement(16);
		bar.setBackground(new Color(0x585858));
		bar.setUI(new MyScrollbarUI());
		contents.add(scrollpane, "name_558142733415764");

		final JPaintComponent contents_back = new JPaintComponent();
		contents_back.setPaints(new GradientPaint(0, 0, new Color(0x262626), 0, 160, new Color(0x161616)));
		contents.add(contents_back, "name_558142749521495");
	}

	private static class MyScrollbarUI extends BasicScrollBarUI {
		private Paint paint = new GradientPaint(0, 0, new Color(0x363636), 0, 120, new Color(0x161616));

		@Override
		protected JButton createDecreaseButton(final int orientation) {
			final JButton button = super.createDecreaseButton(orientation);
			button.setBackground(new Color(0x585858));
			button.setForeground(new Color(0x585858));
			button.setBorder(new EmptyBorder(0, 0, 0, 0));
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setPreferredSize(new Dimension(22, 22));
			return button;
		}

		@Override
		protected void paintTrack(final Graphics graphics, final JComponent c, final Rectangle trackBounds) {
			final Graphics2D g = (Graphics2D) graphics;
			g.setPaint(this.paint);
			g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
		}

		@Override
		protected void paintThumb(final Graphics g, final JComponent c, final Rectangle thumbBounds) {
			if (thumbBounds.isEmpty()||!this.scrollbar.isEnabled())
				return;

			final int w = thumbBounds.width;
			final int h = thumbBounds.height;

			g.translate(thumbBounds.x, thumbBounds.y);
			g.setColor(new Color(0x585858));
			g.fillRect(0, 4, w, h-8);

			g.translate(w/2-3, h/2-3);
			g.setColor(Color.LIGHT_GRAY);
			for (int iy = 0; iy<8; iy += 2)
				for (int ix = 0; ix<8; ix += 2)
					g.drawRect(ix, iy, 0, 0);
		}

		@Override
		protected JButton createIncreaseButton(final int orientation) {
			return createDecreaseButton(orientation);
		}
	}
}
