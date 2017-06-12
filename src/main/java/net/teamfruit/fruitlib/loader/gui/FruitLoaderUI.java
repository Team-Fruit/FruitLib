package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.plaf.LayerUI;

public class FruitLoaderUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final FruitLoaderUI window = new FruitLoaderUI();
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
	public FruitLoaderUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frame = new JFrame();
		this.frame.setBounds(100, 100, 450, 300);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getRootPane().setDoubleBuffered(true);
		((JComponent) this.frame.getContentPane()).setDoubleBuffered(true);

		final JPanel application = new JPanel();
		this.frame.getContentPane().add(application, BorderLayout.CENTER);
		application.setLayout(new BorderLayout(0, 0));

		final JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBackground(new Color(0x1b1464));
		application.add(layeredPane, BorderLayout.CENTER);
		final LoadingSprite loadingSprite = new LoadingSprite();
		loadingSprite.setBounds(0, 0, 128, 250);
		layeredPane.add(loadingSprite);

		final JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 107);
		layeredPane.add(panel);

		final JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 10, 10);
		layeredPane.add(panel_1);
		loadingSprite.load();

		final LayerUI<JPanel> layerUI = new LayerUI<JPanel>();
		final JLayer<JPanel> layer = new JLayer<JPanel>(panel, layerUI);

		final JPanel information = new JPanel();
		application.add(information, BorderLayout.SOUTH);
	}
}