package net.teamfruit.lib;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class FruitClient {
	private static IFruitClient fruit;

	public static void setFruit(final IFruitClient fruitLibClient) {
		if (fruit!=null)
			throw new UnsupportedOperationException("Cannot redefine singleton Fruit instance");
		else
			fruit = fruitLibClient;
	}

	public static IFruitClient getFruit() {
		return fruit;
	}

	private static Minecraft client;

	public static Minecraft client() {
		if (client==null)
			client = FMLClientHandler.instance().getClient();
		return client;
	}

	private static File mcDir;

	public static File mcDir() {
		if (mcDir==null) {
			final Minecraft client = client();
			if (client!=null)
				mcDir = client.mcDataDir;
		}
		if (mcDir==null)
			mcDir = (File) FMLInjectionData.data()[6];
		try {
			mcDir = mcDir.getCanonicalFile();
		} catch (final IOException e) {
		}
		return mcDir;
	}

	private static File fruitDir;

	public static File fruitDir() {
		if (fruitDir==null)
			fruitDir = new File(mcDir(), "fruit");
		return fruitDir;
	}

	public static final Set<String> schemes = ImmutableSet.of("http", "https");

	public static boolean openURL(final @Nonnull URI uri) throws IOException, URISyntaxException {
		final String scheme = StringUtils.lowerCase(uri.getScheme());
		if (!schemes.contains(scheme))
			throw new URISyntaxException(uri.toString(), "Unsupported protocol: "+scheme);
		final Desktop desktop = Desktop.getDesktop();
		desktop.browse(uri);
		return false;
	}

	public static void playSound(final @Nonnull ResourceLocation location, final float volume) {
		client().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(location, volume));
	}
}
