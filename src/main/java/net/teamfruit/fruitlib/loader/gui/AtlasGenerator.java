package net.teamfruit.fruitlib.loader.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.commons.io.input.CloseShieldInputStream;

public class AtlasGenerator {
	public static void main(String args[]) {
		if (args.length==0) {
			final Scanner scanner = new Scanner(new CloseShieldInputStream(System.in));
			args = scanner.nextLine().split(" ");
			scanner.close();
		}
		if (args.length<4) {
			System.out.println("Texture Atlas Generator by Lukasz Bruun - lukasz.dk");
			System.out.println("\tUsage: AtlasGenerator <name> <width> <height> <padding> <scale> <ignorePaths> <unitCoordinates> <directory> [<directory> ...]");
			System.out.println("\t\t<padding>: Padding between images in the final texture atlas.");
			System.out.println("\t\t<ignorePaths>: Only writes out the file name without the path of it to the atlas txt file.");
			System.out.println("\t\t<unitCoordinates>: Coordinates will be written to atlas txt file in 0..1 range instead of 0..width, 0..height range");
			System.out.println("\tExample: AtlasGenerator atlas 2048 2048 5 1 1 images");
			return;
		}

		final AtlasGenerator atlasGenerator = new AtlasGenerator();
		final List<File> dirs = new ArrayList<File>();
		for (int i = 7; i<args.length; ++i)
			dirs.add(new File(args[i]));
		atlasGenerator.Run(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Float.parseFloat(args[4]), Integer.parseInt(args[5])!=0, Integer.parseInt(args[6])!=0, dirs);
	}

	public void Run(final String name, final int width, final int height, final int padding, final float scale, final boolean fileNameOnly, final boolean unitCoordinates, final List<File> dirs) {
		final List<File> imageFiles = new ArrayList<File>();

		for (final File file : dirs) {
			if (!file.exists()||!file.isDirectory()) {
				System.out.println("Error: Could not find directory '"+file.getPath()+"'");
				return;
			}

			GetImageFiles(file, imageFiles);
		}

		System.out.println("Found "+imageFiles.size()+" images");

		final Set<ImageName> imageNameSet = new TreeSet<ImageName>(new ImageNameComparator());

		for (final File f : imageFiles)
			try {
				final BufferedImage image = ImageIO.read(f);

				if (image.getWidth()>width||image.getHeight()>height) {
					System.out.println("Error: '"+f.getPath()+"' ("+image.getWidth()+"x"+image.getHeight()+") is larger than the atlas ("+width+"x"+height+")");
					return;
				}

				final String path = f.getPath().substring(0, f.getPath().lastIndexOf(".")).replace("\\", "/");

				imageNameSet.add(new ImageName(image, path));

			} catch (final IOException e) {
				System.out.println("Could not open file: '"+f.getAbsoluteFile()+"'");
			}

		final List<Texture> textures = new ArrayList<Texture>();

		textures.add(new Texture(width, height));

		int count = 0;

		for (final ImageName imageName : imageNameSet) {
			boolean added = false;

			System.out.println("Adding "+imageName.name+" to atlas ("+(++count)+")");

			for (final Texture texture : textures)
				if (texture.AddImage(imageName.image, imageName.name, padding, scale)) {
					added = true;
					break;
				}

			if (!added) {
				final Texture texture = new Texture(width, height);
				texture.AddImage(imageName.image, imageName.name, padding, scale);
				textures.add(texture);
			}
		}

		count = 0;

		for (final Texture texture : textures) {
			System.out.println("Writing atlas: "+name+(++count));
			texture.Write(name+count, fileNameOnly, unitCoordinates, width, height);
		}
	}

	private void GetImageFiles(final File file, final List<File> imageFiles) {
		if (file.isDirectory()) {
			final File[] files = file.listFiles(new ImageFilenameFilter());
			final File[] directories = file.listFiles(new DirectoryFileFilter());

			imageFiles.addAll(Arrays.asList(files));

			for (final File d : directories)
				GetImageFiles(d, imageFiles);
		}
	}

	private class ImageName {
		public BufferedImage image;
		public String name;

		public ImageName(final BufferedImage image, final String name) {
			this.image = image;
			this.name = name;
		}
	}

	private class ImageNameComparator implements Comparator<ImageName> {
		@Override
		public int compare(final ImageName image1, final ImageName image2) {
			final int area1 = image1.image.getWidth()*image1.image.getHeight();
			final int area2 = image2.image.getWidth()*image2.image.getHeight();

			if (area1!=area2)
				return area2-area1;
			else
				return image1.name.compareTo(image2.name);
		}
	}

	private class ImageFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(final File dir, final String name) {
			return name.toLowerCase().endsWith(".png");
		}
	}

	private class DirectoryFileFilter implements FileFilter {
		@Override
		public boolean accept(final File pathname) {
			return pathname.isDirectory();
		}
	}

	public static BufferedImage resize(final BufferedImage img, final int width, final int height) {
		final BufferedImage thmb = new BufferedImage(width, height, img.getType());
		final Graphics2D g2d = thmb.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2d.drawImage(img, 0, 0, width, height, null);
		return thmb;
	}

	public class Texture {
		private class Node {
			public Rectangle overall;
			public int maxHeight;
			public Rectangle rect;

			public Node(final int x, final int y, final int width, final int height) {
				this.rect = new Rectangle(x, y, width, height);
			}

			public Node Insert(final BufferedImage image, final int padding) {
				this.maxHeight = Math.max(this.maxHeight, image.getHeight());
				if (this.overall==null) {
					final Rectangle o = this.rect;
					this.rect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
					this.overall = o;
					return this;
				}

				if (this.overall.x+image.getWidth()>this.overall.getWidth()-image.getWidth()) {
					final Node node = new Node(this.overall.x = 0, this.overall.y += this.maxHeight, image.getWidth(), image.getHeight());
					this.maxHeight = image.getHeight();
					return node;
				} else {
					final Node node = new Node(this.overall.x += image.getWidth(), this.overall.y, image.getWidth(), image.getHeight());
					return node;
				}
			}
		}

		private class Node2 {
			public Rectangle rect;
			private Node2 child[];
			public BufferedImage image;

			public Node2(final int x, final int y, final int width, final int height) {
				this.rect = new Rectangle(x, y, width, height);
				this.child = new Node2[2];
				this.child[0] = null;
				this.child[1] = null;
				this.image = null;
			}

			public boolean IsLeaf() {
				return this.child[0]==null&&this.child[1]==null;
			}

			// Algorithm from http://www.blackpawn.com/texts/lightmaps/
			public Node2 Insert(final BufferedImage image, final int padding) {
				if (!IsLeaf()) {
					final Node2 newNode = this.child[0].Insert(image, padding);

					if (newNode!=null)
						return newNode;

					return this.child[1].Insert(image, padding);
				} else {
					if (this.image!=null)
						return null; // occupied

					if (image.getWidth()>this.rect.width||image.getHeight()>this.rect.height)
						return null; // does not fit

					if (image.getWidth()==this.rect.width&&image.getHeight()==this.rect.height) {
						this.image = image; // perfect fit
						return this;
					}

					final int dw = this.rect.width-image.getWidth();
					final int dh = this.rect.height-image.getHeight();

					if (dw>dh) {
						this.child[0] = new Node2(this.rect.x, this.rect.y, image.getWidth(), this.rect.height);
						this.child[1] = new Node2(padding+this.rect.x+image.getWidth(), this.rect.y, this.rect.width-image.getWidth()-padding, this.rect.height);
					} else {
						this.child[0] = new Node2(this.rect.x, this.rect.y, this.rect.width, image.getHeight());
						this.child[1] = new Node2(this.rect.x, padding+this.rect.y+image.getHeight(), this.rect.width, this.rect.height-image.getHeight()-padding);
					}

					return this.child[0].Insert(image, padding);
				}
			}
		}

		private BufferedImage image;
		private Graphics2D graphics;
		private Node2 root;
		private Map<String, Rectangle> rectangleMap;

		public Texture(final int width, final int height) {
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			this.graphics = this.image.createGraphics();

			this.root = new Node2(0, 0, width, height);
			this.rectangleMap = new TreeMap<String, Rectangle>();
		}

		public boolean AddImage(BufferedImage image, final String name, final int padding, final float scale) {
			if (Float.compare(scale, 1f)!=0)
				image = resize(image, (int) (image.getWidth()*scale), (int) (image.getHeight()*scale));

			final Node2 node = this.root.Insert(image, padding);

			if (node==null)
				return false;

			this.rectangleMap.put(name, node.rect);
			this.graphics.drawImage(image, null, node.rect.x, node.rect.y);

			return true;
		}

		public void Write(final String name, final boolean fileNameOnly, final boolean unitCoordinates, final int width, final int height) {
			try {
				ImageIO.write(this.image, "png", new File(name+".png"));

				final BufferedWriter atlas = new BufferedWriter(new FileWriter(name+".txt"));

				for (final Map.Entry<String, Rectangle> e : this.rectangleMap.entrySet()) {
					final Rectangle r = e.getValue();
					String keyVal = e.getKey();
					if (fileNameOnly)
						keyVal = keyVal.substring(keyVal.lastIndexOf('/')+1);
					if (unitCoordinates)
						atlas.write(r.x/(float) width+" "+r.y/(float) height+" "+r.width/(float) width+" "+r.height/(float) height);
					else
						atlas.write(r.x+" "+r.y+" "+r.width+" "+r.height);
					atlas.newLine();
				}

				atlas.close();
			} catch (final IOException e) {

			}
		}
	}
}