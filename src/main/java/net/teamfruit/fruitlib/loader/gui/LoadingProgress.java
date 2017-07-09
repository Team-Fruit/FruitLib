package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.google.common.eventbus.Subscribe;

import net.teamfruit.fruitlib.loader.gui.LoaderUIController.TotalSetEvent;
import net.teamfruit.fruitlib.loader.gui.Speed.CurrentSpeed;

public class LoadingProgress extends JPanel {
	private LoaderUIController controller;

	private DetailsGraph graph_detail;

	private JLabel max_speed;

	private JLabel labelCurrentNetwork0;

	private JLabel labelAverageNetwork0;

	private JLabel labelMaxNetwork0;

	private JLabel labelTotal0;

	private Random random = new Random();

	private Timer timer = new Timer((int) TimeUnit.SECONDS.toMillis(2), new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			LoadingProgress.this.controller.getSpeed().update(LoadingProgress.this.random.nextInt(100000));
			final CurrentSpeed currentSpeed = LoadingProgress.this.controller.getSpeed().getSample();
			LoadingProgress.this.graph_detail.addSpeed(currentSpeed).repaint();
			final int max = SizeUnit.SPEED.getMeasure(Math.max(Collections.max(LoadingProgress.this.graph_detail.getShownSpeed()), Collections.max(LoadingProgress.this.graph_detail.getShownAverageSpeed())));
			LoadingProgress.this.max_speed.setText(SizeUnit.SPEED.getFormatSizeString(max, 0));
			final double currentspeed = currentSpeed.getByteSpeed();
			if (currentspeed>=0)
				LoadingProgress.this.labelCurrentNetwork0.setText(SizeUnit.STORAGE.getFormatSizeString(currentspeed, 1)+"/s");
			final double averagespeed = currentSpeed.getAverageByteSpeed();
			if (averagespeed>=0)
				LoadingProgress.this.labelAverageNetwork0.setText(SizeUnit.STORAGE.getFormatSizeString(averagespeed, 1)+"/s");
			final double maxspeed = currentSpeed.getMaxByteSpeed();
			if (maxspeed>=0)
				LoadingProgress.this.labelMaxNetwork0.setText(SizeUnit.STORAGE.getFormatSizeString(maxspeed, 1)+"/s");
			LoadingProgress.this.timer.start();
		}
	});

	@Subscribe
	public void setTotal(final TotalSetEvent event) {
		final long bytes = event.bytes;
		if (bytes>=0)
			LoadingProgress.this.labelTotal0.setText(SizeUnit.STORAGE.getFormatSizeString(bytes, 1));
	}

	/**
	 * Create the panel.
	 * @param controller
	 */
	public LoadingProgress(final LoaderUIController controller) {
		this.controller = controller;
		controller.EVENT_BUS.register(this);

		final JPanel progress = this;
		progress.setBackground(new Color(0x0c1b26));
		progress.setBorder(new EmptyBorder(0, 0, 15, 0));
		progress.setLayout(new BorderLayout(0, 0));
		this.timer.start();

		final JPanel usage_wrap = new JPanel();
		usage_wrap.setOpaque(false);
		progress.add(usage_wrap, BorderLayout.WEST);
		usage_wrap.setLayout(new BorderLayout(0, 0));

		final JPanel usage = new JPanel();
		usage_wrap.add(usage, BorderLayout.SOUTH);
		usage.setOpaque(false);
		usage.setBorder(new EmptyBorder(10, 10, 0, 10));
		usage.setLayout(new BoxLayout(usage, BoxLayout.Y_AXIS));

		final JPanel u_network_wrap = new JPanel();
		u_network_wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
		u_network_wrap.setOpaque(false);
		usage.add(u_network_wrap);
		u_network_wrap.setLayout(new BoxLayout(u_network_wrap, BoxLayout.X_AXIS));

		final JLabel labelNetwork = new JLabel("ネットワーク使用状況");
		labelNetwork.setForeground(Color.WHITE);
		u_network_wrap.add(labelNetwork);
		labelNetwork.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

		final JPanel u_currentnetwork_wrap = new JPanel();
		u_currentnetwork_wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
		u_currentnetwork_wrap.setOpaque(false);
		usage.add(u_currentnetwork_wrap);
		u_currentnetwork_wrap.setLayout(new BoxLayout(u_currentnetwork_wrap, BoxLayout.X_AXIS));

		this.labelCurrentNetwork0 = new JLabel("-- bytes/s");
		this.labelCurrentNetwork0.setForeground(Color.WHITE);
		u_currentnetwork_wrap.add(this.labelCurrentNetwork0);
		this.labelCurrentNetwork0.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

		final JLabel labelCurrentNetwork1 = new JLabel(" 現在");
		labelCurrentNetwork1.setForeground(new Color(0x6f6f6f));
		labelCurrentNetwork1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		u_currentnetwork_wrap.add(labelCurrentNetwork1);

		final JPanel u_averagenetwork_wrap = new JPanel();
		u_averagenetwork_wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
		u_averagenetwork_wrap.setOpaque(false);
		usage.add(u_averagenetwork_wrap);
		u_averagenetwork_wrap.setLayout(new BoxLayout(u_averagenetwork_wrap, BoxLayout.X_AXIS));

		this.labelAverageNetwork0 = new JLabel("-- bytes/s");
		this.labelAverageNetwork0.setForeground(Color.WHITE);
		u_averagenetwork_wrap.add(this.labelAverageNetwork0);
		this.labelAverageNetwork0.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

		final JLabel labelAverageNetwork1 = new JLabel(" 平均");
		labelAverageNetwork1.setForeground(new Color(0x6f6f6f));
		labelAverageNetwork1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		u_averagenetwork_wrap.add(labelAverageNetwork1);

		final JPanel u_maxnetwork_wrap = new JPanel();
		u_maxnetwork_wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
		u_maxnetwork_wrap.setOpaque(false);
		usage.add(u_maxnetwork_wrap);
		u_maxnetwork_wrap.setLayout(new BoxLayout(u_maxnetwork_wrap, BoxLayout.X_AXIS));

		this.labelMaxNetwork0 = new JLabel("-- bytes/s");
		this.labelMaxNetwork0.setForeground(Color.WHITE);
		u_maxnetwork_wrap.add(this.labelMaxNetwork0);
		this.labelMaxNetwork0.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

		final JLabel labelMaxNetwork = new JLabel(" 最大");
		labelMaxNetwork.setForeground(new Color(0x6f6f6f));
		labelMaxNetwork.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		u_maxnetwork_wrap.add(labelMaxNetwork);

		final JPanel u_total_wrap = new JPanel();
		u_total_wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
		u_total_wrap.setOpaque(false);
		usage.add(u_total_wrap);
		u_total_wrap.setLayout(new BoxLayout(u_total_wrap, BoxLayout.X_AXIS));

		this.labelTotal0 = new JLabel("-- bytes");
		this.labelTotal0.setForeground(Color.WHITE);
		u_total_wrap.add(this.labelTotal0);
		this.labelTotal0.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

		final JLabel labelTotal1 = new JLabel(" 合計");
		labelTotal1.setForeground(new Color(0x6f6f6f));
		labelTotal1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		u_total_wrap.add(labelTotal1);

		final JPanel graph = new JPanel();
		graph.setOpaque(false);
		graph.setBorder(new EmptyBorder(10, 10, 0, 0));
		progress.add(graph, BorderLayout.CENTER);
		graph.setLayout(new BorderLayout(0, 0));

		final JPanel graph_wrap = new JPanel();
		graph_wrap.setOpaque(false);
		graph.add(graph_wrap);
		graph_wrap.setLayout(new LayeredLayout());

		this.graph_detail = new DetailsGraph();
		graph_wrap.add(this.graph_detail, "name_550696718695840");
		this.graph_detail.setSpeedColor(new Color(0x183552));
		this.graph_detail.setSpeedAverageColor(new Color(0x3e5a16));
		this.graph_detail.setTextColor(new Color(0x6f6f6f));

		this.graph_detail.init();

		final JPanel graph_overlay = new JPanel();
		graph_overlay.setOpaque(false);
		graph_wrap.add(graph_overlay, "name_550696731555507");
		graph_overlay.setLayout(new BorderLayout(0, 0));

		final JPanel g_top = new JPanel();
		g_top.setOpaque(false);
		graph_overlay.add(g_top, BorderLayout.NORTH);
		g_top.setLayout(new BorderLayout(0, 0));

		final JPanel g_top_left = new JPanel();
		g_top_left.setOpaque(false);
		g_top.add(g_top_left, BorderLayout.WEST);
		g_top_left.setLayout(new BorderLayout(0, 0));

		this.max_speed = new JLabel(" ");
		this.max_speed.setForeground(new Color(0x6f6f6f));
		this.max_speed.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		g_top_left.add(this.max_speed);

		final JPanel g_bottom = new JPanel();
		g_bottom.setOpaque(false);
		graph_overlay.add(g_bottom, BorderLayout.SOUTH);
		g_bottom.setLayout(new BorderLayout(0, 0));

		final JPanel g_bottom_left = new JPanel();
		g_bottom_left.setOpaque(false);
		g_bottom.add(g_bottom_left, BorderLayout.WEST);
		g_bottom_left.setLayout(new BorderLayout(0, 0));

		final JLabel min_speed = new JLabel("0");
		min_speed.setForeground(new Color(0x6f6f6f));
		min_speed.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		g_bottom_left.add(min_speed);

		final JPanel control = new JPanel();
		control.setOpaque(false);
		control.setBorder(new EmptyBorder(10, 10, 0, 10));
		progress.add(control, BorderLayout.EAST);
		control.setLayout(new BorderLayout(0, 0));

		final JPanel pause_panel = new JPanel();
		pause_panel.setOpaque(false);
		control.add(pause_panel, BorderLayout.NORTH);

		final JButton button = new JButton("一時停止");
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setBackground(new Color(0x4f5050));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
			}
		});
		pause_panel.add(button);

		final JPanel legend_wrap = new JPanel();
		legend_wrap.setOpaque(false);
		control.add(legend_wrap, BorderLayout.CENTER);

		final JPanel legend = new JPanel();
		legend.setOpaque(false);
		legend_wrap.add(legend);
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));

		final JPanel l_network_wrap = new JPanel();
		l_network_wrap.setOpaque(false);
		legend.add(l_network_wrap);
		l_network_wrap.setLayout(new BorderLayout(0, 0));

		final JLabel l_color_network = new JLabel("■");
		l_color_network.setForeground(new Color(0x183552));
		l_network_wrap.add(l_color_network, BorderLayout.WEST);

		final JLabel l_network = new JLabel("ネットワーク");
		l_network.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		l_network.setForeground(new Color(0x6f6f6f));
		l_network_wrap.add(l_network);

		final JPanel disk_wrap = new JPanel();
		disk_wrap.setOpaque(false);
		legend.add(disk_wrap);
		disk_wrap.setLayout(new BorderLayout(0, 0));

		final JLabel l_color_disk = new JLabel("■");
		l_color_disk.setForeground(new Color(0x3e5a16));
		disk_wrap.add(l_color_disk, BorderLayout.WEST);

		final JLabel l_disk = new JLabel("ディスク");
		l_disk.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		l_disk.setForeground(new Color(0x6f6f6f));
		disk_wrap.add(l_disk);
	}

}
