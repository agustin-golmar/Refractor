
	package ar.nadezhda.refractor.interfaces;

	import java.io.IOException;

	public interface ImageFormat {

		public byte [][][] getBytes(final String path)
				throws IOException;
		public byte [][][] getBytes(final String path, final int width, final int height)
				throws IOException;

		public String getExtension();
		public int getSupportedChannels();
	}
