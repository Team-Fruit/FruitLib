package net.teamfruit.fruitlib;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.teamfruit.lib.ConfigBase;

public class Config {
	private static @Nullable Config instance;

	public static @Nonnull Config getConfig() {
		if (instance!=null)
			return instance;
		throw new IllegalStateException("config not initialized");
	}

	public static void init(final @Nonnull File cfgFile, final @Nonnull String version) {
		instance = new Config(cfgFile, version);
	}

	private final @Nonnull ConfigBase cfg;

	public @Nonnull ConfigBase getBase() {
		return this.cfg;
	}

	private Config(final @Nonnull File configFile, final @Nonnull String version) {
		this.cfg = new ConfigBase(configFile, version);
	}
}
