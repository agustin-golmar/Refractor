
	package ar.nadezhda.refractor.format;

	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import java.io.IOException;
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
	}
