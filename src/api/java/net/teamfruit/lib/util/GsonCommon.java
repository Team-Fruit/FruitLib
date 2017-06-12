package net.teamfruit.lib.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class GsonCommon {
	public static final @Nonnull Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static <T> T fromJson(final InputStream input, final Class<T> jsonClass) throws JsonSyntaxException, JsonIOException {
		try {
			return gson.fromJson(new InputStreamReader(input, Charsets.UTF_8), jsonClass);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public static <T> void toJson(final OutputStream output, final T data) throws JsonIOException {
		try {
			gson.toJson(data, new OutputStreamWriter(output, Charsets.UTF_8));
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
}
