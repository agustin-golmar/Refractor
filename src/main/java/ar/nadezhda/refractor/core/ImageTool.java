
	package ar.nadezhda.refractor.core;

	public class ImageTool {

		public static byte [][][] rawToImageMatrix(
				final byte [] data, final int offset,
				final int channels, final int width, final int height) {
			final byte [][][] image = new byte [channels][width][height];
			for (int i = 0; i < height; ++i)
				for (int j = 0; j < width; ++j)
					for (int k = 0; k < channels; ++k)
						image[k][j][i] = data[offset + i * width + channels * j + k];
			return image;
		}

		public static int [] getPGMHeader(final byte [] data) {
			// 0 : Data Block Offset
			// 1 : Width
			// 2 : Height
			// 3 : Max. Color
			final int [] metadata = {0, 0, 0, 0};
			final String header = new String(data, 0, 32);
			final String [] values = header.split("\n|\\s+");
			for (int i = 0; i < 32; ++i) {
				if (data[i] == '\n') {
					if (++metadata[0] == 3) {
						metadata[0] = i + 1;
						break;
					}
				}
			}
			metadata[1] = Integer.parseInt(values[1]);
			metadata[2] = Integer.parseInt(values[2]);
			metadata[3] = Integer.parseInt(values[3]);
			return metadata;
		}
	}
