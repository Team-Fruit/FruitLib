package net.teamfruit.fruitlib;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class FruitLauncher {
	public static void main(final String[] args) throws Exception {
		final JFrame jf = new JFrame("Laucher Test");
		final JTextArea ja = new JTextArea();
		ja.setEditable(false);

		try {
			jf.add(new JScrollPane(ja));
			jf.setSize(1000, 1000);
			jf.setLocationRelativeTo(null);
			jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			jf.setVisible(true);

			Class<?> c;
			try {
				c = Class.forName("GradleStart");
			} catch (final Throwable e) {
				c = Class.forName("net.minecraft.launchwrapper.Launch");
			}
			c.getDeclaredMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { args });
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}
}
