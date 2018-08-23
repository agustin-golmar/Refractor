
	package ar.nadezhda.refractor.interfaces;

	import ar.nadezhda.refractor.core.Image;
	import java.io.IOException;

	public interface ImageFormat {

		byte [][][] getBytes(final String path)
				throws IOException;
		byte [][][] getBytes(final String path, final int width, final int height)
				throws IOException;

		String getExtension();
		int getSupportedChannels();

		ImageFormat save(final Image image, final String path)
				throws IOException;
	}
