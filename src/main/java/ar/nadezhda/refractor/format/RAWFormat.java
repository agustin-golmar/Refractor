
	package ar.nadezhda.refractor.format;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import java.io.IOException;
	import java.io.RandomAccessFile;
	import java.nio.file.Files;
	import java.nio.file.Paths;

	public class RAWFormat implements ImageFormat {

		@Override
		public byte [][][] getBytes(final String path)
				throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public byte [][][] getBytes(final String path, final int width, final int height)
				throws IOException {
			final byte [] data = Files.readAllBytes(Paths.get(path));
			return ImageTool.rawToImageMatrix(
					data, 0,
					getSupportedChannels(), width, height);
		}

		@Override
		public String getExtension() {
			return "raw";
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
				for (int h = 0; h < image.getHeight(); ++h)
					for (int w = 0; w < image.getWidth(); ++w) {
						output.writeByte(image.rawGray()[w][h]);
					}
			}
			return this;
		}
	}
