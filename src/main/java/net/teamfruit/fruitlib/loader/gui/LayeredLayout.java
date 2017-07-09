package net.teamfruit.fruitlib.loader.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.List;

import com.google.common.collect.Lists;

public class LayeredLayout implements LayoutManager2 {
	private final List<Component> comps = Lists.newArrayList();

	@Override
	public void addLayoutComponent(final Component comp, final Object constraints) {
		synchronized (comp.getTreeLock()) {
			this.comps.add(comp);
		}
	}

	@Override
	public void addLayoutComponent(final String name, final Component comp) {
		synchronized (comp.getTreeLock()) {
			this.comps.add(comp);
		}
	}

	@Override
	public void removeLayoutComponent(final Component comp) {
		synchronized (comp.getTreeLock()) {
			this.comps.remove(comp);
		}
	}

	@Override
	public Dimension preferredLayoutSize(final Container target) {
		synchronized (target.getTreeLock()) {
			final Dimension dim = new Dimension(0, 0);

			for (final Component c : this.comps) {
				final Dimension d = c.getPreferredSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height = Math.max(d.height, dim.height);
			}

			final Insets insets = target.getInsets();
			dim.width += insets.left+insets.right;
			dim.height += insets.top+insets.bottom;

			return dim;
		}
	}

	@Override
	public Dimension minimumLayoutSize(final Container target) {
		synchronized (target.getTreeLock()) {
			final Dimension dim = new Dimension(0, 0);

			for (final Component c : this.comps) {
				final Dimension d = c.getMinimumSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height = Math.max(d.height, dim.height);
			}

			final Insets insets = target.getInsets();
			dim.width += insets.left+insets.right;
			dim.height += insets.top+insets.bottom;

			return dim;
		}
	}

	@Override
	public Dimension maximumLayoutSize(final Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public void layoutContainer(final Container target) {
		synchronized (target.getTreeLock()) {
			final Insets insets = target.getInsets();
			final int top = insets.top;
			final int bottom = target.getHeight()-insets.bottom;
			final int left = insets.left;
			final int right = target.getWidth()-insets.right;

			for (final Component c : this.comps)
				c.setBounds(left, top, right-left, bottom-top);
		}
	}

	@Override
	public float getLayoutAlignmentX(final Container parent) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(final Container parent) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(final Container target) {
	}
}
