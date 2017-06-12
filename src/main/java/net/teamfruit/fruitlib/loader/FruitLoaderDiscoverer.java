package net.teamfruit.fruitlib.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.teamfruit.lib.loader.PathFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;

public class FruitLoaderDiscoverer {
	public final SortedSetMultimap<String, FruitLoaderModData> fruitMods = Multimaps.newSortedSetMultimap(
			Maps.<String, Collection<FruitLoaderModData>> newTreeMap(),
			new Supplier<TreeSet<FruitLoaderModData>>() {
				@Override
				public TreeSet<FruitLoaderModData> get() {
					return Sets.newTreeSet();
				}
			});
	public final SortedSetMultimap<String, FruitLoaderDependencyData> dependencies = Multimaps.newSortedSetMultimap(
			Maps.<String, Collection<FruitLoaderDependencyData>> newTreeMap(),
			new Supplier<TreeSet<FruitLoaderDependencyData>>() {
				@Override
				public TreeSet<FruitLoaderDependencyData> get() {
					return Sets.newTreeSet();
				}
			});
	public final Map<String, File> transformers = Maps.newHashMap();
	public final Set<String> plugins = Sets.newHashSet();

	public void loadMods(final File modsDir) {
		final FileFilter modFilter = new FileFilter() {
			@Override
			public boolean accept(final File file) {
				if (file.isDirectory())
					return true;
				final String name = file.getName();
				return name.endsWith(".jar")||name.endsWith(".zip");
			}
		};
		final File[] mods = modsDir.listFiles(modFilter);
		for (final File mod : mods)
			loadMod(mod);
	}

	public boolean loadMod(final File mod) {
		PathFile modArchive = null;
		InputStream manifestInput = null;
		try {
			manifestInput = (modArchive = PathFile.create(mod)).getEntry("fruit.json").getInputStream();
			if (manifestInput!=null) {
				final FruitLoaderModData manifest = FruitLoaderModData.fromJson(manifestInput);
				if (manifest!=null) {
					manifest.setFile(mod);
					if (StringUtils.isEmpty(manifest.id)||StringUtils.isEmpty(manifest.version)) {
						Log.log.warn("fruit manifest data is invalid. ignored: "+manifest);
						return false;
					}
					final String transformer = manifest.transformer;
					if (!StringUtils.isEmpty(transformer))
						this.transformers.put(transformer, mod);
					final String plugin = manifest.plugin;
					if (!StringUtils.isEmpty(plugin))
						this.plugins.add(plugin);
					if (manifest.dependencies!=null)
						for (final FruitLoaderDependencyData dependency : manifest.dependencies) {
							if (dependency.id==null) {
								Log.log.warn("dependency data is invalid. ignored: "+dependency);
								continue;
							}
							dependency.setRequired(manifest);
							this.dependencies.put(dependency.id, dependency);
						}
					this.fruitMods.put(manifest.id, manifest);

				}
			}
		} catch (final IOException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(manifestInput);
			IOUtils.closeQuietly(modArchive);
		}
		return false;
	}
}
