
	package ar.nadezhda.refractor.core;

	import java.awt.Color;

	public class Image {

		public static int GRAY_LEVELS = 256;

		// Channels:
		public static int GRAY = 0;
		public static int RED = 0;
		public static int HUE = 0;
		public static int GREEN = 1;
		public static int SATURATION = 1;
		public static int BLUE = 2;
		public static int BRIGHTNESS = 2;
		public static int VALUE = 2;

		// Con el formato [channel][w][h]:
		public final double [][][] data;
		public final byte [][][] rawData;

		protected final String source;

		public Image(final String source, final byte [][][] raw) {
			this.source = source;
			this.data = new double [raw.length][raw[0].length][raw[0][0].length];
			this.rawData = raw;
			for (int h = 0; h < getHeight(); ++h)
				for (int w = 0; w < getWidth(); ++w)
					for (int c = 0; c < getChannels(); ++c) {
						this.data[c][w][h] = Byte.toUnsignedInt(raw[c][w][h]);
					}
		}

		public Image(
				final String source, final int channels,
				final int width, final int height) {
			this.source = source;
			this.data = new double [channels][width][height];
			this.rawData = new byte [channels][width][height];
		}

		public int getChannels() {
			return data.length;
		}

		public int getWidth() {
			return data[0].length;
		}

		public int getHeight() {
			return data[0][0].length;
		}

		public int getSize() {
			return getWidth() * getHeight();
		}

		public double [][] gray() {
			return data[GRAY];
		}

		public double [][] red() {
			return data[RED];
		}

		public double [][] green() {
			return data[GREEN];
		}

		public double [][] blue() {
			return data[BLUE];
		}

		public byte [][] rawGray() {
			return rawData[GRAY];
		}

		public byte [][] rawRed() {
			return rawData[RED];
		}

		public byte [][] rawGreen() {
			return rawData[GREEN];
		}

		public byte [][] rawBlue() {
			return rawData[BLUE];
		}

		public String getSource() {
			return source;
		}

		public int [][] getRawHistogram() {
			final int [][] histogram = new int [getChannels()][GRAY_LEVELS];
			for (int h = 0; h < getHeight(); ++h)
				for (int w = 0; w < getWidth(); ++w)
					for (int c = 0; c < getChannels(); ++c) {
						++histogram[c][Byte.toUnsignedInt(rawData[c][w][h])];
					}
			return histogram;
		}

		public Image getGrayscale() {
			final byte [][][] raw = new byte [1][getWidth()][getHeight()];
			if (getChannels() == 3) {
				for (int h = 0; h < getHeight(); ++h)
					for (int w = 0; w < getWidth(); ++w) {
						final int red = Byte.toUnsignedInt(rawRed()[w][h]);
						final int green = Byte.toUnsignedInt(rawGreen()[w][h]);
						final int blue = Byte.toUnsignedInt(rawBlue()[w][h]);
						final double brightness = Color.RGBtoHSB(red, green, blue, null)[BRIGHTNESS];
						raw[0][w][h] = (byte) ((GRAY_LEVELS - 1) * brightness);
					}
			}
			else {
				for (int h = 0; h < getHeight(); ++h)
					for (int w = 0; w < getWidth(); ++w)
						raw[0][w][h] = rawGray()[w][h];
			}
			return new Image(source, raw);
		}
	}
