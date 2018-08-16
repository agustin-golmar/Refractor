
	package ar.nadezhda.refractor.format;

	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import java.io.IOException;
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
	}
