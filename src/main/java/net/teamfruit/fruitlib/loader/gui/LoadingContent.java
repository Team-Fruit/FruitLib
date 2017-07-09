package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.teamfruit.fruitlib.loader.gui.LoaderUIController.LoadingContentController;

public class LoadingContent extends JPanel implements LoadingContentController {
	private JLabel progress_1_value;
	private JLabel progress_2_value;
	private JLabel progress_3_value;
	private JLabel progress_4_value;
	private boolean isPause;
	private JLabel title_1;
	private FitImage image_1;
	private JProgressBar progressBar;

	/**
	 * Create the panel.
	 */
	public LoadingContent() {
		setLayout(new BorderLayout(0, 0));

		final JPanel view = new JPanel();
		view.setOpaque(false);
		add(view, BorderLayout.CENTER);

		final JPanel thumbnail = new JPanel();
		thumbnail.setOpaque(false);

		final JPanel information = new JPanel();
		information.setOpaque(false);

		final JPanel controls = new JPanel();
		controls.setOpaque(false);

		final JPanel status = new JPanel();
		status.setOpaque(false);
		final GroupLayout gl_view = new GroupLayout(view);
		gl_view.setHorizontalGroup(
				gl_view.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_view.createSequentialGroup()
								.addContainerGap()
								.addComponent(thumbnail, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(information, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(status, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));
		gl_view.setVerticalGroup(
				gl_view.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_view.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_view.createParallelGroup(Alignment.TRAILING)
										.addComponent(status, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
										.addComponent(information, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
										.addComponent(controls, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
										.addComponent(thumbnail, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addContainerGap()));
		thumbnail.setLayout(new BorderLayout(0, 0));

		final JPanel image_wrap = new JPanel();
		image_wrap.setOpaque(false);
		thumbnail.add(image_wrap);
		image_wrap.setLayout(new BorderLayout(0, 0));

		this.image_1 = new FitImage();
		/*try {
			image.setImage(new ImageIcon(new URL("https://i.gyazo.com/91bc578643d91a11f259f3e258a47ad9.png")).getImage());
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
		}*/
		image_wrap.add(this.image_1);
		status.setLayout(new BorderLayout(0, 0));

		final JPanel s_left = new JPanel();
		s_left.setOpaque(false);
		status.add(s_left, BorderLayout.WEST);
		final GridBagLayout gbl_s_left = new GridBagLayout();
		gbl_s_left.columnWidths = new int[] { 0 };
		gbl_s_left.rowHeights = new int[] { 0 };
		gbl_s_left.columnWeights = new double[] { 0.0 };
		gbl_s_left.rowWeights = new double[] { 0.0 };
		s_left.setLayout(gbl_s_left);

		final JPanel progress_wrap = new JPanel();
		progress_wrap.setOpaque(false);
		final GridBagConstraints gbc_progress_wrap = new GridBagConstraints();
		gbc_progress_wrap.anchor = GridBagConstraints.NORTHWEST;
		gbc_progress_wrap.gridx = 0;
		gbc_progress_wrap.gridy = 0;
		s_left.add(progress_wrap, gbc_progress_wrap);
		progress_wrap.setLayout(new BoxLayout(progress_wrap, BoxLayout.Y_AXIS));

		final JPanel progress_1 = new JPanel() {
			@Override
			public boolean isVisible() {
				return !LoadingContent.this.progress_1_value.getText().isEmpty();
			}
		};
		progress_1.setOpaque(false);
		progress_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		progress_wrap.add(progress_1);
		progress_1.setLayout(new BoxLayout(progress_1, BoxLayout.X_AXIS));

		final JLabel progress_1_lbl = new JLabel("ダウンロード済み  ");
		progress_1.add(progress_1_lbl);
		progress_1_lbl.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_1_lbl.setForeground(new Color(0x6f6f6f));

		this.progress_1_value = new JLabel("87.8MB / 820.5MB");
		progress_1.add(this.progress_1_value);
		this.progress_1_value.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		this.progress_1_value.setForeground(Color.WHITE);

		final JPanel progress_2 = new JPanel() {
			@Override
			public boolean isVisible() {
				return !LoadingContent.this.progress_2_value.getText().isEmpty();
			}
		};
		progress_2.setOpaque(false);
		progress_2.setAlignmentX(Component.LEFT_ALIGNMENT);
		progress_wrap.add(progress_2);
		progress_2.setLayout(new BoxLayout(progress_2, BoxLayout.X_AXIS));

		final JLabel progress_2_lbl = new JLabel("開始時間  ");
		progress_2_lbl.setForeground(new Color(111, 111, 111));
		progress_2_lbl.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_2.add(progress_2_lbl);

		this.progress_2_value = new JLabel("8:52");
		this.progress_2_value.setForeground(Color.WHITE);
		this.progress_2_value.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_2.add(this.progress_2_value);

		final JPanel progress_3 = new JPanel() {
			@Override
			public boolean isVisible() {
				return !LoadingContent.this.progress_3_value.getText().isEmpty();
			}
		};
		progress_3.setOpaque(false);
		progress_3.setAlignmentX(Component.LEFT_ALIGNMENT);
		progress_wrap.add(progress_3);
		progress_3.setLayout(new BoxLayout(progress_3, BoxLayout.X_AXIS));

		final JLabel progress_3_lbl = new JLabel("残り時間  ");
		progress_3_lbl.setForeground(new Color(111, 111, 111));
		progress_3_lbl.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_3.add(progress_3_lbl);

		this.progress_3_value = new JLabel("3 時間 24 分");
		this.progress_3_value.setForeground(Color.WHITE);
		this.progress_3_value.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_3.add(this.progress_3_value);

		final JPanel progress_4 = new JPanel() {
			@Override
			public boolean isVisible() {
				return !LoadingContent.this.progress_4_value.getText().isEmpty();
			}
		};
		progress_4.setOpaque(false);
		progress_4.setAlignmentX(Component.LEFT_ALIGNMENT);
		progress_wrap.add(progress_4);
		progress_4.setLayout(new BoxLayout(progress_4, BoxLayout.X_AXIS));

		final JLabel progress_4_lbl = new JLabel("完了した時間  ");
		progress_4_lbl.setForeground(new Color(111, 111, 111));
		progress_4_lbl.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_4.add(progress_4_lbl);

		this.progress_4_value = new JLabel("11:29");
		this.progress_4_value.setForeground(Color.WHITE);
		this.progress_4_value.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_4.add(this.progress_4_value);

		final JPanel progress_5 = new JPanel() {
			@Override
			public boolean isVisible() {
				return LoadingContent.this.isPause;
			}
		};
		progress_5.setOpaque(false);
		progress_5.setAlignmentX(Component.LEFT_ALIGNMENT);
		progress_wrap.add(progress_5);
		progress_5.setLayout(new BoxLayout(progress_5, BoxLayout.X_AXIS));

		final JLabel progress_5_lbl = new JLabel("一時停止");
		progress_5_lbl.setForeground(new Color(111, 111, 111));
		progress_5_lbl.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		progress_5.add(progress_5_lbl);

		information.setLayout(new BorderLayout(0, 0));

		final JPanel i_left = new JPanel();
		i_left.setOpaque(false);
		information.add(i_left, BorderLayout.WEST);
		i_left.setLayout(new BorderLayout(0, 0));

		final JPanel i_left_top = new JPanel();
		i_left_top.setOpaque(false);
		i_left.add(i_left_top, BorderLayout.NORTH);
		i_left_top.setLayout(new BorderLayout(0, 0));

		final JPanel title_wrap = new JPanel();
		i_left_top.add(title_wrap, BorderLayout.NORTH);
		title_wrap.setOpaque(false);
		title_wrap.setLayout(new BoxLayout(title_wrap, BoxLayout.Y_AXIS));

		this.title_1 = new JLabel("SignPicture");
		this.title_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
		this.title_1.setForeground(Color.WHITE);
		title_wrap.add(this.title_1);

		final JPanel progress = new JPanel() {
			@Override
			public boolean isVisible() {
				return LoadingContent.this.progressBar.getMaximum()>0;
			}
		};
		progress.setAlignmentX(Component.LEFT_ALIGNMENT);
		progress.setBorder(new EmptyBorder(5, 0, 5, 0));
		progress.setOpaque(false);
		title_wrap.add(progress);
		progress.setLayout(new BorderLayout(0, 0));

		this.progressBar = new JProgressBar();
		this.progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.progressBar.setPreferredSize(new Dimension(120, 3));
		this.progressBar.setBackground(Color.BLACK);
		this.progressBar.setForeground(new Color(0x1e4266));
		this.progressBar.setUI(new MyProgressUI());
		this.progressBar.setMaximum(0);
		progress.add(this.progressBar, BorderLayout.NORTH);
		final GridBagLayout gbl_controls = new GridBagLayout();
		gbl_controls.columnWidths = new int[] { 0, 0, 0 };
		gbl_controls.rowHeights = new int[] { 0, 0, 0 };
		gbl_controls.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_controls.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		controls.setLayout(gbl_controls);

		final JPanel middle_right = new JPanel();
		middle_right.setOpaque(false);
		final GridBagConstraints gbc_middle_right = new GridBagConstraints();
		gbc_middle_right.gridheight = 0;
		gbc_middle_right.gridwidth = 0;
		controls.add(middle_right, gbc_middle_right);
		middle_right.setLayout(new BorderLayout(0, 0));

		final JPanel inqueue_wrap = new JPanel();
		inqueue_wrap.setOpaque(false);
		middle_right.add(inqueue_wrap, BorderLayout.NORTH);

		final JLabel inqueue = new JLabel("キュー内");
		inqueue_wrap.add(inqueue);
		inqueue.setHorizontalAlignment(SwingConstants.RIGHT);
		inqueue.setForeground(Color.WHITE);
		inqueue.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));

		final JPanel ctrl_wrap = new JPanel();
		ctrl_wrap.setOpaque(false);
		middle_right.add(ctrl_wrap, BorderLayout.CENTER);
		ctrl_wrap.setLayout(new BorderLayout(0, 0));

		final JPanel ctrl = new JPanel();
		ctrl.setOpaque(false);
		ctrl_wrap.add(ctrl);
		ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.X_AXIS));

		final JPanel ctrl_panel = new JPanel();
		ctrl_panel.setOpaque(false);
		ctrl.add(ctrl_panel);

		final JButton cancel = new JButton("X");
		cancel.setMargin(new Insets(0, 0, 0, 0));
		cancel.setPreferredSize(new Dimension(25, 25));
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
			}
		});
		ctrl_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final JButton dlnow = new JButton("↑");
		dlnow.setMargin(new Insets(0, 0, 0, 0));
		dlnow.setPreferredSize(new Dimension(25, 25));
		dlnow.setForeground(Color.WHITE);
		dlnow.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		dlnow.setBorderPainted(false);
		dlnow.setFocusPainted(false);
		dlnow.setBackground(new Color(0x4f5050));
		ctrl_panel.add(dlnow);
		cancel.setForeground(Color.WHITE);
		cancel.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
		cancel.setBorderPainted(false);
		cancel.setFocusPainted(false);
		cancel.setBackground(new Color(0x4f5050));
		ctrl_panel.add(cancel);
		view.setLayout(gl_view);

	}

	private static class MyProgressUI extends BasicProgressBarUI {

		private Rectangle r = new Rectangle();

		@Override
		protected void paintIndeterminate(final Graphics g, final JComponent c) {
			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			this.r = getBox(this.r);
			g.setColor(this.progressBar.getForeground());
			g.fillOval(this.r.x, this.r.y, this.r.width, this.r.height);
		}
	}

	@Override
	public LoadingContent getInternalContent() {
		return this;
	}

	@Override
	public void setTitle(final String title) {
		this.title_1.setText(title);
	}

	@Override
	public String getTitle() {
		return this.title_1.getText();
	}

	@Override
	public void setImage(final Image image) {
		this.image_1.setImage(image);
	}

	@Override
	public Image getImage() {
		return this.image_1.getImage();
	}

	@Override
	public void setProgressMaximum(final int maximum) {
		this.progressBar.setMaximum(maximum);
		updateProgressText();
	}

	@Override
	public int getProgressMaximum() {
		return this.progressBar.getMaximum();
	}

	@Override
	public void setProgressValue(final int value) {
		this.progressBar.setValue(value);
		updateProgressText();
	}

	@Override
	public int getProgressValue() {
		return this.progressBar.getValue();
	}

	private void updateProgressText() {
		final int max = this.progressBar.getMaximum();
		if (max<=0) {
			this.progress_1_value.setText("");
			return;
		}
		final int value = this.progressBar.getValue();
		final String text = SizeUnit.STORAGE.getFormatSizeString(value, 1)+" / "+SizeUnit.STORAGE.getFormatSizeString(max, 1);
		this.progress_1_value.setText(text);
	}

	@Override
	public void setPause(final boolean pause) {
		this.isPause = pause;
	}

	@Override
	public boolean isPause() {
		return this.isPause;
	}

	@Override
	public void setStartedTime(final String time) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String getStartedTime() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void setRemainingTime(final String time) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String getRemainingTime() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void setDoneTime(final String time) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String getDoneTime() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
