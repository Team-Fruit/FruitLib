package net.teamfruit.fruitlib.loader;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

public class AbstractFruitLoaderArtifact implements Comparable<AbstractFruitLoaderArtifact> {
	public String id;
	public String version;
	private transient ArtifactVersion artifactVersion;

	public AbstractFruitLoaderArtifact() {
	}

	public AbstractFruitLoaderArtifact(final String id, final String version) {
		this.id = id;
		this.version = version;
	}

	public ArtifactVersion artifactVersion() {
		if (this.artifactVersion==null)
			this.artifactVersion = new DefaultArtifactVersion(this.version);
		return this.artifactVersion;
	}

	@Override
	public int compareTo(final AbstractFruitLoaderArtifact o) {
		return artifactVersion().compareTo(o.artifactVersion());
	}

	public String getInfo() {
		return this.id+"@"+this.version;
	}
}
