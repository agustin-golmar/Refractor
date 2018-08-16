
	package ar.nadezhda.refractor.interfaces;

	import ar.nadezhda.refractor.core.Image;
	import java.io.IOException;

	public interface ImageFormat {

		public byte [][][] getBytes(final String path)
				throws IOException;
		public byte [][][] getBytes(final String path, final int width, final int height)
				throws IOException;

		public String getExtension();
		public int getSupportedChannels();

		public ImageFormat save(final Image image, final String path)
				throws IOException;
	}
