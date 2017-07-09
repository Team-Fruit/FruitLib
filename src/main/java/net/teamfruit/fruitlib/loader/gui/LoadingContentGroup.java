package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class LoadingContentGroup extends JPanel {
	private JPanel contents;
	private JLabel lbl_enqueued;

	/**
	 * Create the panel.
	 */
	public LoadingContentGroup() {
		setLayout(new BorderLayout(0, 0));

		final JPaintComponent hr1 = new JPaintComponent() {
			@Override
			protected Paint[] getPaints() {
				return new Paint[] {
						new GradientPaint(0, 0, new Color(0x4b4b4b), getWidth(), 0, new Color(0x262626))
				};
			}

			@Override
			public boolean isVisible() {
				return LoadingContentGroup.this.contents.getComponents().length>0;
			}
		};
		add(hr1, BorderLayout.NORTH);
		hr1.setBorder(new EmptyBorder(4, 5, 1, 5));
		hr1.setLayout(new BorderLayout(0, 0));

		this.lbl_enqueued = new JLabel("");
		this.lbl_enqueued.setFont(new Font("Yu Gothic UI", Font.PLAIN, 11));
		hr1.add(this.lbl_enqueued, BorderLayout.NORTH);
		this.lbl_enqueued.setForeground(new Color(0xa8a8a8));

		this.contents = new JPanel();
		this.contents.setOpaque(false);
		add(this.contents, BorderLayout.CENTER);
		this.contents.setLayout(new BoxLayout(this.contents, BoxLayout.Y_AXIS));

		final LoadingContent content1 = new LoadingContent();
		this.contents.add(content1);
		content1.setOpaque(false);
	}

	public JPanel getContents() {
		return this.contents;
	}

	public String getLabel() {
		return this.lbl_enqueued.getText();
	}

	public void setLabel(final String label) {
		this.lbl_enqueued.setText(label);
	}
}
