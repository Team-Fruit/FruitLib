package net.teamfruit.fruitlib.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.client.gui.GuiScreen;
import net.teamfruit.fruitlib.FruitLibHooks;
import net.teamfruit.lib.asm.DescHelper;
import net.teamfruit.lib.asm.MethodMatcher;

public class GuiScreenVisitor extends ClassVisitor {
	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			/*
			 * 0  aload_0 [screen]
			 * 1  invokestatic net.teamfruit.fruitlib.FruitLibHooks.onGuiKeyInput(net.minecraft.client.gui.GuiScreen) : boolean [10]
			 * 4  ifeq 8
			 * 7  return
			 */
			super.visitVarInsn(Opcodes.ALOAD, 0);
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/teamfruit/fruitlib/FruitLibHooks", "onGuiKeyInput", DescHelper.toDesc(boolean.class, "net.minecraft.client.gui.GuiScreen"), false);
			final Label label = new Label();
			super.visitJumpInsn(Opcodes.IFEQ, label);
			super.visitInsn(Opcodes.RETURN);
			super.visitLabel(label);
			super.visitCode();
		}
	}

	private final MethodMatcher matcher;

	public static void testInvoke(final GuiScreen screen) {
		if (FruitLibHooks.onGuiKeyInput(screen))
			return;
		"".equals("");
		return;
	}

	public GuiScreenVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		this.matcher = new MethodMatcher(obfClassName, DescHelper.toDesc(void.class, new Object[0]), ASMDeobfNames.GuiScreenHandleInput);
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		return this.matcher.match(name, desc) ? new HookMethodVisitor(parent) : parent;
	}
}