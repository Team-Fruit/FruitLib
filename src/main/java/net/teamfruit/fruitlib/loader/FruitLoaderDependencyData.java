package net.teamfruit.fruitlib.loader;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.teamfruit.lib.util.GsonCommon;

public class FruitLoaderDependencyData extends AbstractFruitLoaderArtifact {
	public String url;
	private transient FruitLoaderModData required;

	public FruitLoaderDependencyData() {
	}

	public FruitLoaderDependencyData(final String id, final String version, final String dependencyurl) {
		super(id, version);
		this.url = dependencyurl;
	}

	public FruitLoaderDependencyData setRequired(final FruitLoaderModData from) {
		this.required = from;
		return this;
	}

	public FruitLoaderModData required() {
		return this.required;
	}

	public static FruitLoaderDependencyData fromJson(final InputStream input) throws JsonSyntaxException, JsonIOException {
		return GsonCommon.fromJson(input, FruitLoaderDependencyData.class);
	}

	public static void toJson(final OutputStream output, final FruitLoaderDependencyData data) throws JsonIOException {
		GsonCommon.toJson(output, data);
	}

	@Override
	public String toString() {
		return String.format("FruitLoaderDependencyData [id=%s, version=%s, dependencyurl=%s]", this.id, this.version, this.url);
	}

	@Override
	public String getInfo() {
		return this.id+"@"+this.version+" ["+this.url+"]";
	}

	public String getInfoAll() {
		final StringBuilder stb = new StringBuilder(getInfo());
		final FruitLoaderModData required = required();
		if (required!=null) {
			stb.append(" is dependent on ").append(required.getInfo());
			final File file = required.file();
			if (file!=null)
				stb.append(" [").append(file.getName()).append("]");
		}
		return stb.toString();
	}
}
