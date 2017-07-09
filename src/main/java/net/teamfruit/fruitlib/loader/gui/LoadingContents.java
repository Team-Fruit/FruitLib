package net.teamfruit.fruitlib.loader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.JPanel;

public class LoadingContents extends JPanel {

	/**
	 * Create the panel.
	 * @param controller
	 */
	public LoadingContents(final LoaderUIController controller) {
		setLayout(new BorderLayout(0, 0));

		final JPanel view = new JPanel();
		view.setOpaque(false);
		add(view, BorderLayout.NORTH);
		view.setLayout(new BorderLayout(0, 0));

		final Box view_list = Box.createVerticalBox();
		view_list.setOpaque(false);
		view.add(view_list, BorderLayout.NORTH);
		view_list.setOpaque(false);

		final JPanel staging = new JPanel();
		staging.setOpaque(false);

		final LoadingContentGroup enqueued = new LoadingContentGroup();
		enqueued.setOpaque(false);
		enqueued.setLabel("キューに追加済み");

		final LoadingContentGroup failed = new LoadingContentGroup();
		failed.setOpaque(false);
		failed.setLabel("完了");

		final LoadingContentGroup done = new LoadingContentGroup();
		done.setOpaque(false);
		done.setLabel("失敗");
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
		view_list.add(staging);
		view_list.add(enqueued);
		view_list.add(failed);
		view_list.add(done);
		view_list.revalidate();
	}
}
