
	package ar.nadezhda.refractor.core;

	public class Image {

		public static int RED = 0;
		public static int GREEN = 1;
		public static int BLUE = 2;

		// Con el formato [channel][x][y]:
		public final double [][][] data;
		public final byte [][][] rawData;

		public Image(final byte [][][] raw) {
			this.data = new double [raw.length][raw[0].length][raw[0][0].length];
			this.rawData = raw;
			for (int i = 0; i < getChannels(); ++i)
				for (int j = 0; j < getWidth(); ++j)
					for (int k = 0; k < getHeight(); ++k)
						this.data[i][j][k] = Byte.toUnsignedInt(raw[i][j][k]);
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

		public double [][] red() {
			return data[RED];
		}

		public double [][] green() {
			return data[GREEN];
		}

		public double [][] blue() {
			return data[BLUE];
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
