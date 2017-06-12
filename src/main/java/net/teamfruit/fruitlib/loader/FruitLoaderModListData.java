package net.teamfruit.fruitlib.loader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.teamfruit.lib.util.GsonCommon;

public class FruitLoaderModListData {
	public String id;
	public String version;

	public List<FruitLoaderDependencyData> dependencies;

	public FruitLoaderModListData() {
	}

	public FruitLoaderModListData(final String id, final String version, final List<FruitLoaderDependencyData> dependencies) {
		this.id = id;
		this.version = version;
		this.dependencies = dependencies;
	}

	public static FruitLoaderModListData fromJson(final InputStream input) throws JsonSyntaxException, JsonIOException {
		return GsonCommon.fromJson(input, FruitLoaderModListData.class);
	}

	public static void toJson(final OutputStream output, final FruitLoaderModListData data) throws JsonIOException {
		GsonCommon.toJson(output, data);
	}
}
