
	package ar.nadezhda.refractor.format;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import java.io.IOException;
	import java.io.RandomAccessFile;
	import java.nio.file.Files;
	import java.nio.file.Paths;

	public class PGMFormat implements ImageFormat {

		@Override
		public byte [][][] getBytes(final String path)
				throws IOException {
			final byte [] data = Files.readAllBytes(Paths.get(path));
			final int [] header = ImageTool.getPGMHeader(data);
			return ImageTool.rawToImageMatrix(
					data, header[0],
					getSupportedChannels(), header[1], header[2]);
		}

		@Override
		public byte [][][] getBytes(final String path, final int width, final int height)
				throws IOException {
			return getBytes(path);
		}

		@Override
		public String getExtension() {
			return "pgm";
		}

		@Override
		public int getSupportedChannels() {
			return 1;
		}

		@Override
		public ImageFormat save(final Image image, final String path)
				throws IOException {
			try (final RandomAccessFile output = new RandomAccessFile(path, "rw")) {
				output.setLength(0);
				output.writeBytes("P5\n" + image.getWidth() + " " + image.getHeight() + "\n255\n");
				for (int h = 0; h < image.getHeight(); ++h)
					for (int w = 0; w < image.getWidth(); ++w) {
						output.writeByte(image.rawGray()[w][h]);
					}
			}
			return this;
		}
	}
