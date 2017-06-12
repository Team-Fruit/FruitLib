package net.teamfruit.lib.loader;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class PathFile implements Closeable {
	public abstract PathEntry getEntry(String entry);

	public abstract File getFile();

	public static PathFile create(final File base) throws IOException {
		if (base.isDirectory())
			return new FilePathFile(base);
		else
			return new ZipPathFile(base);
	}

	private static class FilePathFile extends PathFile {
		private final File base;

		public FilePathFile(final File base) {
			this.base = base;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public PathEntry getEntry(final String entry) {
			return new FilePathEntry(new File(this.base, entry));
		}

		@Override
		public File getFile() {
			return this.base;
		}

		private static class FilePathEntry implements PathEntry {
			private final File entry;

			public FilePathEntry(final File entry) {
				this.entry = entry;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return new FileInputStream(this.entry);
			}
		}
	}

	private static class ZipPathFile extends FilePathFile {
		private final ZipFile zipbase;

		public ZipPathFile(final File base) throws IOException {
			super(base);
			this.zipbase = new ZipFile(base);
		}

		@Override
		public void close() throws IOException {
			this.zipbase.close();
		}

		@Override
		public PathEntry getEntry(final String entry) {
			return new ZipPathEntry(this.zipbase, this.zipbase.getEntry(entry));
		}

		private static class ZipPathEntry implements PathEntry {
			private final ZipFile file;
			private final ZipEntry entry;

			public ZipPathEntry(final ZipFile file, final ZipEntry entry) {
				this.file = file;
				this.entry = entry;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return this.file.getInputStream(this.entry);
			}
		}
	}

	public static interface PathEntry {
		InputStream getInputStream() throws IOException;
	}
}
