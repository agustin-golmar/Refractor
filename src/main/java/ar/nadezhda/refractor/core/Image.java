
	package ar.nadezhda.refractor.core;

	public class Image {

		public static int GRAY = 0;
		public static int RED = 0;
		public static int GREEN = 1;
		public static int BLUE = 2;

		// Con el formato [channel][w][h]:
		public final double [][][] data;
		public final byte [][][] rawData;

		public Image(final byte [][][] raw) {
			this.data = new double [raw.length][raw[0].length][raw[0][0].length];
			this.rawData = raw;
			for (int h = 0; h < getHeight(); ++h)
				for (int w = 0; w < getWidth(); ++w)
					for (int c = 0; c < getChannels(); ++c) {
						this.data[c][w][h] = Byte.toUnsignedInt(raw[c][w][h]);
						this.data[c][w][h] /= 255.0;
					}
		}

		public Image(final int channels, final int width, final int height) {
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
	}
