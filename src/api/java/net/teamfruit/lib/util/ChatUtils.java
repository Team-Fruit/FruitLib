package net.teamfruit.lib.util;

import javax.annotation.Nonnull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.teamfruit.lib.FruitClient;
import net.teamfruit.lib.FruitCommon;

public class ChatUtils {
	public static ChatBuilder builder() {
		return new ChatBuilder();
	}

	@SideOnly(Side.CLIENT)
	public static void sendClient(final @Nonnull IChatComponent msg, final int id) {
		FruitClient.client().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(msg, id);
	}

	@SideOnly(Side.CLIENT)
	public static void sendClient(final @Nonnull IChatComponent msg) {
		final EntityPlayer player = FruitClient.client().thePlayer;
		if (player!=null)
			player.addChatComponentMessage(msg);
	}

	public static void sendPlayer(final @Nonnull ICommandSender target, final @Nonnull IChatComponent msg) {
		target.addChatMessage(msg);
	}

	public static void sendServer(final @Nonnull IChatComponent msg) {
		FruitCommon.server().getConfigurationManager().sendChatMsg(msg);
	}
}
