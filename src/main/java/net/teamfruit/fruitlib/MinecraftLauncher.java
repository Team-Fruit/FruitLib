package net.teamfruit.fruitlib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.launchwrapper.Launch;
import net.teamfruit.lib.Log;

public class MinecraftLauncher {
	public static class ProcessArgumentFactory {
		private String processArg;

		public ProcessArgumentFactory withJava() {
			this.processArg = "\""+System.getProperty("java.home")+"/bin/java"+"\"";
			return this;
		}

		public ProcessArgumentFactory withJavaw() {
			this.processArg = "\""+System.getProperty("java.home")+"/bin/javaw"+"\"";
			return this;
		}

		public ProcessArgumentFactory withProcess(final String arguments) {
			this.processArg = arguments;
			return this;
		}

		public List<String> getArguments() {
			if (this.processArg==null)
				throw new IllegalStateException("missing process argument");
			final List<String> processArguments = Lists.newArrayList(this.processArg);
			return processArguments;
		}
	}

	public static class JvmArgumentFactory {
		private final List<String> jvmArgs = Lists.newArrayList();

		public JvmArgumentFactory withCurrentArguments() {
			final RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
			final List<String> jvmArguments0 = runtimeMxBean.getInputArguments();
			this.jvmArgs.addAll(jvmArguments0);
			return this;
		}

		public JvmArgumentFactory withArgument(final String argument) {
			this.jvmArgs.add(argument);
			return this;
		}

		public JvmArgumentFactory ignoreXDebug() {
			for (final Iterator<String> itr = this.jvmArgs.iterator(); itr.hasNext();) {
				final String arg = itr.next();
				if (StringUtils.contains(arg, "jdwp")&&StringUtils.contains(arg, "dt_socket")&&StringUtils.contains(arg, "address"))
					itr.remove();
			}
			return this;
		}

		public List<String> getArguments() {
			return this.jvmArgs;
		}
	}

	public static class ClasspathArgumentFactory {
		private String launchClass;
		private final List<String> libraries = Lists.newArrayList();

		public ClasspathArgumentFactory withLaunchClass() {
			try {
				Class.forName("GradleStart");
				withGradleStartLaunchClass();
			} catch (final Exception e) {
				withLaunchWrapperLaunchClass();
			}
			return this;
		}

		public ClasspathArgumentFactory withGradleStartLaunchClass() {
			this.launchClass = "GradleStart";
			return this;
		}

		public ClasspathArgumentFactory withLaunchWrapperLaunchClass() {
			this.launchClass = "net.minecraft.launchwrapper.Launch";
			return this;
		}

		public ClasspathArgumentFactory withLaunchClass(final String launchClass) {
			this.launchClass = launchClass;
			return this;
		}

		public ClasspathArgumentFactory withCurrentLibraries() throws IOException, URISyntaxException {
			final File mcDataDir = (File) FMLInjectionData.data()[6];
			final File modsDir = new File(mcDataDir, "mods");
			final File modsDirCanonical = modsDir.getCanonicalFile();
			final URL[] libraries0 = Launch.classLoader.getURLs();
			final List<String> libraries = Lists.newArrayList();
			for (final URL library : libraries0) {
				final String pathCanonical = new File(library.toURI()).getCanonicalPath();
				if (!FileUtils.directoryContains(modsDirCanonical, new File(pathCanonical)))
					libraries.add(pathCanonical);
			}
			this.libraries.addAll(libraries);
			return this;
		}

		public ClasspathArgumentFactory withClassLocationLibraries(final Class<?> clazz) throws IOException, URISyntaxException {
			this.libraries.add(getClassLocation(clazz).getAbsolutePath());
			return this;
		}

		public ClasspathArgumentFactory withLibrary(final String library) {
			this.libraries.add(library);
			return this;
		}

		public List<String> getArguments() {
			if (this.launchClass==null)
				withLaunchClass();

			final List<String> classpathArguments = Lists.newArrayList();
			classpathArguments.add("-classpath");
			classpathArguments.add("\""+StringUtils.join(this.libraries, File.pathSeparator)+File.pathSeparator+"\"");
			classpathArguments.add(this.launchClass);
			return classpathArguments;
		}

		private static File getClassLocation(final Class<?> clazz) throws IOException, URISyntaxException {
			final URI classCurrentPathURI = clazz.getResource("").toURI();
			final String fsClassCurrentPath = StringUtils.equalsIgnoreCase(classCurrentPathURI.getScheme(), "jar") ? StringUtils.substringBefore(classCurrentPathURI.getSchemeSpecificPart(), "!") : classCurrentPathURI.toString();
			final URI fsClassCurrentPathURI = new URI(fsClassCurrentPath);
			final File fsClassCurrentPathFile = new File(fsClassCurrentPathURI);
			final File fsClassCurrentPathFileCanonical = fsClassCurrentPathFile.getCanonicalFile();

			final URL[] pathURLs = ((URLClassLoader) clazz.getClassLoader()).getURLs();
			File classLocation = null;
			c: for (final URL pathURL : pathURLs) {
				final File pathFile = new File(pathURL.toURI()).getCanonicalFile();
				if (FileUtils.directoryContains(pathFile, fsClassCurrentPathFileCanonical)) {
					File f = fsClassCurrentPathFileCanonical;
					do
						if (pathFile.equals(f)) {
							classLocation = pathFile;
							break c;
						}
					while ((f = f.getParentFile())!=null);
				}
			}
			return classLocation;
		}
	}

	public static class CommandlineArgumentFactory {
		private final List<String> commandlineArgs = Lists.newArrayList();

		public CommandlineArgumentFactory withCurrentArguments() {
			@SuppressWarnings("unchecked")
			final List<String> commandlineArguments0 = (List<String>) Launch.blackboard.get("ArgumentList");
			this.commandlineArgs.addAll(commandlineArguments0);
			return this;
		}

		public CommandlineArgumentFactory withArgument(final String arg) {
			this.commandlineArgs.add(arg);
			return this;
		}

		public CommandlineArgumentFactory withArguments(final String key, final String value) {
			this.commandlineArgs.add(key);
			this.commandlineArgs.add(value);
			return this;
		}

		public CommandlineArgumentFactory withTweakClass(final String tweakClass) {
			this.commandlineArgs.add("--tweakClass");
			this.commandlineArgs.add(tweakClass);
			return this;
		}

		public CommandlineArgumentFactory withFMLTweakClass() {
			try {
				Class.forName("net.minecraftforge.fml.common.launcher.FMLTweaker");
				withForgeFMLTweakClass();
			} catch (final Exception e) {
				withCpwFMLTweakClass();
			}
			return this;
		}

		public CommandlineArgumentFactory withForgeFMLTweakClass() {
			return withTweakClass("net.minecraftforge.fml.common.launcher.FMLTweaker");
		}

		public CommandlineArgumentFactory withCpwFMLTweakClass() {
			return withTweakClass("cpw.mods.fml.common.launcher.FMLTweaker");
		}

		public List<String> getArguments() {
			return this.commandlineArgs;
		}
	}

	public static class LaunchArgumentFactory {
		private List<String> processArgs;
		private List<String> jvmArgs;
		private List<String> classpathArgs;
		private List<String> commandlineArgs;

		public LaunchArgumentFactory withProcessArguments(final List<String> arguments) {
			this.processArgs = arguments;
			return this;
		}

		public LaunchArgumentFactory withProcessArguments(final ProcessArgumentFactory factory) {
			return withProcessArguments(factory.getArguments());
		}

		public LaunchArgumentFactory withJvmArguments(final List<String> arguments) {
			this.jvmArgs = arguments;
			return this;
		}

		public LaunchArgumentFactory withJvmArguments(final JvmArgumentFactory factory) {
			return withJvmArguments(factory.getArguments());
		}

		public LaunchArgumentFactory withClasspathArguments(final List<String> arguments) {
			this.classpathArgs = arguments;
			return this;
		}

		public LaunchArgumentFactory withClasspathArguments(final ClasspathArgumentFactory factory) {
			return withClasspathArguments(factory.getArguments());
		}

		public LaunchArgumentFactory withCommandlineArguments(final List<String> arguments) {
			this.commandlineArgs = arguments;
			return this;
		}

		public LaunchArgumentFactory withCommandlineArguments(final CommandlineArgumentFactory factory) {
			return withCommandlineArguments(factory.getArguments());
		}

		public List<String> getArguments() throws IOException, URISyntaxException {
			final List<String> arguments = Lists.newArrayList();

			if (this.processArgs==null)
				this.processArgs = new ProcessArgumentFactory().withJavaw().getArguments();
			arguments.addAll(this.processArgs);

			if (this.jvmArgs==null)
				this.jvmArgs = new JvmArgumentFactory().withCurrentArguments().ignoreXDebug().getArguments();
			arguments.addAll(this.jvmArgs);

			if (this.classpathArgs==null)
				this.classpathArgs = new ClasspathArgumentFactory().withCurrentLibraries().withLaunchClass().getArguments();
			arguments.addAll(this.classpathArgs);

			if (this.commandlineArgs==null)
				this.commandlineArgs = new CommandlineArgumentFactory().withCurrentArguments().withFMLTweakClass().getArguments();
			arguments.addAll(this.commandlineArgs);

			return arguments;
		}
	}

	public static void launch() throws IOException, URISyntaxException {
		final LaunchArgumentFactory factory = new LaunchArgumentFactory();

		final List<String> arguments = factory.getArguments();

		launch(arguments);
	}

	public static void launch2() throws IOException, URISyntaxException {
		final File fruitlibDir = new File(FileUtils.getTempDirectory(), "FruitLib");
		final File newClasspathDir = new File(fruitlibDir, "Launcher");
		final File newClassDir = new File(newClasspathDir, "net/teamfruit/lib");
		final File newClassLocation = new File(newClassDir, "FruitLauncher.class");

		if (newClassDir.exists()) {
			if (newClassDir.isDirectory()==false)
				throw new IOException("Destination '"+newClassDir+"' exists but is not a directory");
		} else if (!newClassDir.mkdirs()&&!newClassDir.isDirectory())
			throw new IOException("Destination '"+newClassDir+"' directory cannot be created");

		InputStream input = null;
		try {
			IOUtils.copyLarge(input = MinecraftLauncher.class.getResource("FruitLauncher.class").openStream(), new FileOutputStream(newClassLocation));
		} finally {
			IOUtils.closeQuietly(input);
		}

		final LaunchArgumentFactory factory = new LaunchArgumentFactory()
				.withClasspathArguments(new ClasspathArgumentFactory()
						.withCurrentLibraries()
						.withLibrary(newClasspathDir.getAbsolutePath())
						.withLaunchClass("net.teamfruit.lib.FruitLauncher"));

		final List<String> arguments = factory.getArguments();

		launch(arguments);

		Log.log.info(StringUtils.join(arguments, " "));
	}

	public static void launch(final List<String> arguments) throws IOException, URISyntaxException {
		final ProcessBuilder pb = new ProcessBuilder(arguments);

		try {
			pb.getClass().getDeclaredMethod("inheritIO", new Class[] {}).invoke(pb, new Object[] {});
		} catch (final Exception e) {
			Log.log.warn("inheritIO is not supported in Java 6");
		}

		pb.start();
	}
}
