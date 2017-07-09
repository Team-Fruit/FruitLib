package net.teamfruit.fruitlib.loader.gui.sprite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.teamfruit.lib.swing.ComponentMover;

public class FruitLoadingSpriteUI {

	private JFrame frame;

	private JPanel application;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final FruitLoadingSpriteUI window = new FruitLoadingSpriteUI();
					window.frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FruitLoadingSpriteUI() {
		initialize();
	}

	/*
	private Timer timer = new Timer(20, new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final float p = (float) ((Math.sin(System.nanoTime()*0.0000000001f)+1)/2);
			Log.log.info(p);
			final Color a = Color.white;
			final Color b = new Color(0x98C1D9);
			FruitLoaderUI.this.application.setBackground(new Color((int) (a.getRed()*(1-p)+b.getRed()*p), (int) (a.getGreen()*(1-p)+b.getGreen()*p), (int) (a.getBlue()*(1-p)+b.getBlue()*p)));
			FruitLoaderUI.this.timer.start();
		}
	});
	*/

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frame = new JFrame();
		this.frame.setUndecorated(true);
		new ComponentMover(this.frame, this.frame);
		this.frame.setSize(225, 300);
		this.frame.setLocationRelativeTo(null);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getRootPane().setDoubleBuffered(true);
		((JComponent) this.frame.getContentPane()).setDoubleBuffered(true);

		this.application = new JPanel();
		this.application.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.application.setBackground(new Color(255, 255, 255));
		// this.timer.start();
		this.frame.getContentPane().add(this.application, BorderLayout.CENTER);
		this.application.setLayout(new BorderLayout(0, 0));

		final JPanel center = new JPanel();
		center.setOpaque(false);
		this.application.add(center, BorderLayout.CENTER);
		final GridBagLayout gbl_center = new GridBagLayout();
		center.setLayout(gbl_center);

		final JPanel titlepanel = new JPanel();
		titlepanel.setOpaque(false);
		final GridBagConstraints gbc_titlepanel = new GridBagConstraints();
		gbc_titlepanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_titlepanel.gridx = 0;
		gbc_titlepanel.gridy = 0;
		center.add(titlepanel, gbc_titlepanel);
		titlepanel.setLayout(new BorderLayout(0, 0));
		final LoadingSprite loadingSprite = new LoadingSprite();
		titlepanel.add(loadingSprite, BorderLayout.CENTER);

		final JPanel text = new JPanel();
		text.setOpaque(false);
		titlepanel.add(text, BorderLayout.SOUTH);

		final JLabel lblLoading = new JLabel("Loading...");
		lblLoading.setFont(new Font("Dialog", Font.PLAIN, 12));
		text.add(lblLoading);
		loadingSprite.load();
	}
}