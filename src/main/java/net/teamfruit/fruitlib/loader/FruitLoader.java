package net.teamfruit.fruitlib.loader;

import static net.teamfruit.lib.reflect.ReflectionUtil.*;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Queues;
import com.google.common.primitives.Ints;

import cpw.mods.fml.common.asm.transformers.ModAccessTransformer;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.ModListHelper;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.teamfruit.lib.launch.FruitTransformer;
import net.teamfruit.lib.reflect.ReflectionUtil._Method;
import net.teamfruit.lib.util.Downloader;
import net.teamfruit.lib.util.Progress;

public class FruitLoader {
	public final FruitLoaderDiscoverer discoverer = new FruitLoaderDiscoverer();

	public FruitLoader() {
	}

	public void load() throws Exception {
		final File mcDir = mcDir();
		final File modsDir = new File(mcDir, "mods");
		this.discoverer.loadMods(modsDir);
		final Map<String, SortedSet<FruitLoaderDependencyData>> dependencies = Multimaps.asMap(this.discoverer.dependencies);
		final Deque<FruitLoaderDependencyData> latestdependencies = Queues.newArrayDeque();
		for (final Entry<String, SortedSet<FruitLoaderDependencyData>> dependency : dependencies.entrySet()) {
			final SortedSet<FruitLoaderDependencyData> treeset = dependency.getValue();
			final FruitLoaderDependencyData last = treeset.last();
			if (last==null)
				continue;
			final FruitLoaderDependencyData first = treeset.first();
			if (first!=null&&!last.equals(first))
				if (!last.artifactVersion().containsVersion(first.artifactVersion())) {
					Log.log.warn("Incompatible dependencies found. Latest one will be used causing problems. continuing...");
					Log.log.warn("    - "+last.getInfoAll());
					Log.log.warn("    - "+first.getInfoAll());
				}
			latestdependencies.offer(last);
		}

		FruitLoaderDependencyData latestdependency;
		update: while ((latestdependency = latestdependencies.poll())!=null)
			try {
				Log.log.info("Start downloading: "+latestdependency.getInfoAll());
				// download(latestdependency.dependencyurl, modsDir, new Progress());
				final Collection<FruitLoaderModData> olds = this.discoverer.fruitMods.get(latestdependency.id);
				for (final FruitLoaderModData old : olds) {
					if (latestdependency.compareTo(old)<=0)
						continue update;
					final File oldFile = old.file();
					unlockMod(oldFile);
					FileUtils.deleteQuietly(oldFile);
				}
				final File dest = downloadTest(new File(latestdependency.url), modsDir);
				discoverCoreMod(modsDir, dest, Launch.classLoader);
				Log.log.info("Finish downloading: "+latestdependency.getInfoAll());
			} catch (final Exception e) {
				Log.log.error(e.getMessage(), e);
			}

		for (final Entry<String, File> transformer : this.discoverer.transformers.entrySet())
			FruitTransformer.instance.addTransformerName(transformer.getKey(), transformer.getValue());
	}

	public static File downloadTest(final File from, final File dir) {
		try {
			final File dest = new File(dir, from.getName());
			if (from.isDirectory())
				FileUtils.copyDirectory(from, dest);
			else
				FileUtils.copyFile(from, dest);
			return dest;
		} catch (final IOException e) {
			Log.log.error(e.getMessage(), e);
		}
		return null;
	}

	public static File download(final String url, final File dir, final Progress progress) {
		InputStream input = null;
		OutputStream output = null;
		try {
			final HttpUriRequest req = new HttpGet(url);
			final HttpClientContext context = HttpClientContext.create();
			final HttpResponse response = Downloader.downloader.client.execute(req, context);

			String name = null;
			name: {
				final Header header = response.getFirstHeader("Content-disposition");
				if (header==null)
					break name;
				final HeaderElement[] elements = header.getElements();
				if (elements.length==0)
					break name;
				final NameValuePair pair = elements[0].getParameterByName("filename");
				if (pair==null)
					break name;
				name = pair.getValue();
			}

			if (name==null) {
				final String url1 = StringUtils.removeEnd(url, "/");
				name = StringUtils.substringAfterLast(url1, "/");
			}

			final File dest = new File(dir, name);

			final HttpEntity entity = response.getEntity();

			progress.setOverall(entity.getContentLength());
			input = entity.getContent();
			output = new CountingOutputStream(new FileOutputStream(dest)) {
				@Override
				protected void afterWrite(final int n) throws IOException {
					progress.setDone(getByteCount());
				}
			};
			IOUtils.copyLarge(input, output);

			return dest;
		} catch (final Exception e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		return null;
	}

	public static File mcDir() throws IOException {
		final File mcDir0 = (File) FMLInjectionData.data()[6];
		return mcDir0.getCanonicalFile();
	}

	public static void discoverCoreMod(final File coreMods, final File coreMod, final LaunchClassLoader classLoader) throws Exception {
		FMLRelaunchLog.fine("Examining for coremod candidacy %s", coreMod.getName());
		JarFile jar = null;
		Attributes mfAttributes;
		String fmlCorePlugin;

		final File versionedModDir = new File(coreMods, (String) FMLInjectionData.data()[4]);

		final List<String> ignoredModFiles = _pfield(CoreModManager.class, $("ignoredModFiles", "loadedCoremods")).$get(null);
		final List<String> candidateModFiles = _pfield(CoreModManager.class, $("candidateModFiles", "reparsedCoremods")).$get(null);

		final Attributes.Name COREMODCONTAINSFMLMOD = _pfield(CoreModManager.class, $("COREMODCONTAINSFMLMOD")).$get(null);
		final Attributes.Name MODTYPE = _pfield(CoreModManager.class, $("MODTYPE")).$get(null);
		final Attributes.Name MODSIDE = _pfield(CoreModManager.class, $("MODSIDE")).$get(null);

		final _Method handleCascadingTweak = _pmethod(CoreModManager.class, $("handleCascadingTweak"), File.class, JarFile.class, String.class, LaunchClassLoader.class, Integer.class);
		final _Method loadCoreMod = _pmethod(CoreModManager.class, $("loadCoreMod"), LaunchClassLoader.class, String.class, File.class);

		try {
			jar = new JarFile(coreMod);
			if (jar.getManifest()==null)
				// Not a coremod and no access transformer list
				return;
			ModAccessTransformer.addJar(jar);
			mfAttributes = jar.getManifest().getMainAttributes();
			final String cascadedTweaker = mfAttributes.getValue("TweakClass");
			if (cascadedTweaker!=null) {
				FMLRelaunchLog.info("Loading tweaker %s from %s", cascadedTweaker, coreMod.getName());
				Integer sortOrder = Ints.tryParse(Strings.nullToEmpty(mfAttributes.getValue("TweakOrder")));
				sortOrder = sortOrder==null ? Integer.valueOf(0) : sortOrder;
				handleCascadingTweak.$invoke(null, coreMod, jar, cascadedTweaker, classLoader, sortOrder);
				ignoredModFiles.add(coreMod.getName());
				return;
			}

			final List<String> modTypes = mfAttributes.containsKey(MODTYPE) ? Arrays.asList(mfAttributes.getValue(MODTYPE).split(",")) : ImmutableList.of("FML");

			if (!modTypes.contains("FML")) {
				FMLRelaunchLog.fine("Adding %s to the list of things to skip. It is not an FML mod,  it has types %s", coreMod.getName(), modTypes);
				ignoredModFiles.add(coreMod.getName());
				return;
			}

			final String modSide = mfAttributes.containsKey(MODSIDE) ? mfAttributes.getValue(MODSIDE) : "BOTH";
			if (!("BOTH".equals(modSide)||FMLLaunchHandler.side().name().equals(modSide))) {
				FMLRelaunchLog.fine("Mod %s has ModSide meta-inf value %s, and we're %s. It will be ignored", coreMod.getName(), modSide, FMLLaunchHandler.side().name());
				ignoredModFiles.add(coreMod.getName());
				return;
			}
			final _Method extractContainedDepJars = __pmethod(CoreModManager.class, $("extractContainedDepJars"), JarFile.class, File.class, File.class);
			if (extractContainedDepJars!=null)
				ModListHelper.additionalMods.putAll(extractContainedDepJars.<Map<? extends String, ? extends File>> $invoke(null, jar, coreMods, versionedModDir));
			fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
			if (fmlCorePlugin==null) {
				// Not a coremod
				FMLRelaunchLog.fine("Not found coremod data in %s", coreMod.getName());
				return;
			}
		} catch (final IOException ioe) {
			FMLRelaunchLog.log(Level.ERROR, ioe, "Unable to read the jar file %s - ignoring", coreMod.getName());
			return;
		} finally {
			if (jar!=null)
				try {
					jar.close();
				} catch (final IOException e) {
					// Noise
				}
		}
		// Support things that are mod jars, but not FML mod jars
		try {
			classLoader.addURL(coreMod.toURI().toURL());
			if (!mfAttributes.containsKey(COREMODCONTAINSFMLMOD)) {
				FMLRelaunchLog.finer("Adding %s to the list of known coremods, it will not be examined again", coreMod.getName());
				ignoredModFiles.add(coreMod.getName());
			} else {
				FMLRelaunchLog.finer("Found FMLCorePluginContainsFMLMod marker in %s, it will be examined later for regular @Mod instances",
						coreMod.getName());
				candidateModFiles.add(coreMod.getName());
			}
		} catch (final MalformedURLException e) {
			FMLRelaunchLog.log(Level.ERROR, e, "Unable to convert file into a URL. weird");
			return;
		}
		loadCoreMod.$invoke(null, classLoader, fmlCorePlugin, coreMod);
	}

	public static void unlockMod(final File modfile) {
		try {
			Log.log.info("Unlock "+modfile.getName());
			final LaunchClassLoader cloader = Launch.classLoader;
			final URL url = modfile.toURI().toURL();
			final Field f_ucp = $pfield(URLClassLoader.class, $("ucp"));
			final Class<?> c_ucp = $class($("sun.misc.URLClassPath"), cloader);
			final Field f_loaders = $pfield(c_ucp, $("loaders"));
			final Field f_lmap = $pfield(c_ucp, $("lmap"));
			final Class<?> c_urlutl = $class($("sun.net.util.URLUtil"), cloader);
			final Method m_urlnofragstr = $method(c_urlutl, $("urlNoFragString"), URL.class);

			final Object ucp = f_ucp.get(cloader);
			final Object urlnofragstr = m_urlnofragstr.invoke(null, url);
			final Closeable loader = (Closeable) ((Map<?, ?>) f_lmap.get(ucp)).remove(urlnofragstr);
			if (loader!=null) {
				loader.close();
				((List<?>) f_loaders.get(ucp)).remove(loader);
			}
		} catch (final Exception e2) {
			Log.log.error(e2);
		}
	}

}
