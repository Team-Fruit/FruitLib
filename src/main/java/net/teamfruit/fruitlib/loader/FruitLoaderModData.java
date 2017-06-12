package net.teamfruit.fruitlib.loader;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.teamfruit.lib.util.GsonCommon;

public class FruitLoaderModData extends AbstractFruitLoaderArtifact {
	public String logo;
	public String transformer;
	public String plugin;
	private transient File file;

	public List<FruitLoaderDependencyData> dependencies;

	public FruitLoaderModData() {
	}

	public FruitLoaderModData(final String id, final String version, final String logo, final List<FruitLoaderDependencyData> dependencies) {
		super(id, version);
		this.logo = logo;
		this.dependencies = dependencies;
	}

	public FruitLoaderModData setFile(final File file) {
		this.file = file;
		return this;
	}

	public File file() {
		return this.file;
	}

	public static FruitLoaderModData fromJson(final InputStream input) throws JsonSyntaxException, JsonIOException {
		return GsonCommon.fromJson(input, FruitLoaderModData.class);
	}

	public static void toJson(final OutputStream output, final FruitLoaderModData data) throws JsonIOException {
		GsonCommon.toJson(output, data);
	}

	@Override
	public String toString() {
		return String.format("FruitLoaderModData [id=%s, version=%s, logo=%s, dependencies=%s]", this.id, this.version, this.logo, this.dependencies);
	}
}