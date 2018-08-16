
	package ar.nadezhda.refractor.format;

	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import java.awt.image.BufferedImage;
	import java.awt.image.DataBufferByte;
	import java.io.File;
	import java.io.IOException;
	import javax.imageio.ImageIO;

	public class BMPFormat implements ImageFormat {

		@Override
		public byte [][][] getBytes(final String path)
				throws IOException {
			final BufferedImage image = ImageIO
					.read(new File(path));
			final DataBufferByte buffer = (DataBufferByte) image
					.getRaster()
					.getDataBuffer();
			return ImageTool.rawToImageMatrix(
					buffer.getData(), 0,
					getSupportedChannels(), image.getWidth(), image.getHeight());
		}

		@Override
		public byte [][][] getBytes(final String path, int width, int height)
				throws IOException {
			return getBytes(path);
		}

		@Override
		public String getExtension() {
			return "bmp";
		}

		@Override
		public int getSupportedChannels() {
			return 3;
		}
	}
