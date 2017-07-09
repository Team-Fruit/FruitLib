package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Rectangle2D;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class LoadingContents extends JPanel {

	/**
	 * Create the panel.
	 */
	public LoadingContents() {
		setLayout(new BorderLayout(0, 0));

		final JPanel view = new JPanel();
		view.setOpaque(false);
		add(view, BorderLayout.CENTER);

		final JPanel enqueued = new JPanel();
		enqueued.setOpaque(false);

		final JPanel failed = new JPanel();
		failed.setOpaque(false);

		final JPanel staging = new JPanel();
		staging.setOpaque(false);

		final GroupLayout gl_view = new GroupLayout(view);
		gl_view.setHorizontalGroup(
				gl_view.createParallelGroup(Alignment.LEADING)
						.addComponent(enqueued, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
						.addComponent(failed, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
						.addComponent(staging, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE));
		gl_view.setVerticalGroup(
				gl_view.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_view.createSequentialGroup()
								.addComponent(staging, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(enqueued, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(failed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		staging.setLayout(new BorderLayout(0, 0));

		final JPanel stage_wrap = new JPaintComponent() {
			@Override
			protected Paint[] getPaints() {
				return new Paint[] {
						new GradientPaint(0, 0, new Color(0x0d1e2a), 0, getHeight(), new Color(0x1e4466)),
						new RadialGradientPaint(
								new Rectangle2D.Double(0, 0, getWidth(), getHeight()),
								new float[] { .3f, 1f },
								new Color[] { new Color(0x1f2225), new Color(0f, 0f, 0f, 0f) },
								CycleMethod.NO_CYCLE)
				};
			}
		};
		staging.add(stage_wrap, BorderLayout.CENTER);
		stage_wrap.setLayout(new BorderLayout(0, 0));

		final LoadingContent content1 = new LoadingContent();
		content1.setOpaque(false);
		stage_wrap.add(content1);
		enqueued.setLayout(new BoxLayout(enqueued, BoxLayout.Y_AXIS));

		final JPaintComponent hr1 = new JPaintComponent() {
			@Override
			protected Paint[] getPaints() {
				return new Paint[] {
						new GradientPaint(0, 0, new Color(0x4b4b4b), getWidth(), 0, new Color(0x262626))
				};
			}
		};
		enqueued.add(hr1);
		hr1.setBorder(new EmptyBorder(4, 5, 1, 5));
		hr1.setLayout(new BorderLayout(0, 0));

		final JLabel lbl_enqueued = new JLabel("キューに追加済み");
		lbl_enqueued.setFont(new Font("Yu Gothic UI", Font.PLAIN, 11));
		hr1.add(lbl_enqueued, BorderLayout.NORTH);
		lbl_enqueued.setForeground(new Color(0xa8a8a8));

		final LoadingContent content2 = new LoadingContent();
		content2.setOpaque(false);
		enqueued.add(content2);
		view.setLayout(gl_view);
		failed.setLayout(new BoxLayout(failed, BoxLayout.Y_AXIS));

		final JPaintComponent hr2 = new JPaintComponent() {
			@Override
			protected Paint[] getPaints() {
				return new Paint[] {
						new GradientPaint(0, 0, new Color(0x4b4b4b), getWidth(), 0, new Color(0x262626))
				};
			}
		};
		failed.add(hr2);
		hr2.setBorder(new EmptyBorder(4, 5, 1, 5));
		hr2.setLayout(new BorderLayout(0, 0));

		final JLabel lbl_failed = new JLabel("失敗");
		lbl_failed.setFont(new Font("Yu Gothic UI", Font.PLAIN, 11));
		hr2.add(lbl_failed, BorderLayout.NORTH);
		lbl_failed.setForeground(new Color(0xa8a8a8));

		final LoadingContent content3 = new LoadingContent();
		content3.setOpaque(false);
		failed.add(content3);
		view.setLayout(gl_view);

	}
}
