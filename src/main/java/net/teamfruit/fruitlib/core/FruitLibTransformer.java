package net.teamfruit.fruitlib.core;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;
import net.teamfruit.lib.Log;
import net.teamfruit.lib.asm.VisitorHelper;
import net.teamfruit.lib.asm.VisitorHelper.TransformProvider;

public class FruitLibTransformer implements IClassTransformer {
	@Override
	public @Nullable byte[] transform(final @Nullable String name, final @Nullable String transformedName, final @Nullable byte[] bytes) {
		if (bytes==null||name==null||transformedName==null)
			return bytes;

		if (transformedName.equals("net.minecraft.client.gui.GuiScreen"))
			return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
				@Override
				public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
					Log.log.info(String.format("Patching GuiScreen.handleInput (class: %s)", name));
					return new GuiScreenVisitor(name, cv);
				}
			});

		return bytes;
	}
}