
	package ar.nadezhda.refractor.core;

	import ar.nadezhda.refractor.support.Matrix;
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
        private double[] mean;
        //private double[] stdDev;

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

		public Image(final String source, final double [][][] data) {
			this.source = source;
			this.data = data;
			this.rawData = new byte [data.length][data[0].length][data[0][0].length];
			for (int h = 0; h < getHeight(); ++h)
				for (int w = 0; w < getWidth(); ++w)
					for (int c = 0; c < getChannels(); ++c) {
						this.rawData[c][w][h] = (byte) data[c][w][h];
					}
		}

		public double [][][] getEmptyImageSpace() {
			return Matrix.emptySpaceFrom(data);
		}

		public byte [][][] getRawEmptyImageSpace() {
			return new byte [getChannels()][getWidth()][getHeight()];
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

		public double [][] getNormalizedHistogram() {
			final int [][] histogram = getRawHistogram();
			final double [][] normalized = new double [getChannels()][GRAY_LEVELS];
			final double N = getSize();
			for (int g = 0; g < GRAY_LEVELS; ++g)
				for (int c = 0; c < getChannels(); ++c) {
					normalized[c][g] = histogram[c][g] / N;
				}
			return normalized;
		}

		public double [][] getCummulativeHistogram() {
			final double [][] normalized = getNormalizedHistogram();
			for (int g = 1; g < GRAY_LEVELS; ++g)
				for (int c = 0; c < getChannels(); ++c) {
					normalized[c][g] += normalized[c][g - 1];
				}
			return normalized;
		}

		public double [][] getCummulativeMean(){
            final double[][] normalized = getNormalizedHistogram();
            for (int c=0;c<getChannels();++c){
                for (int g=0;g<GRAY_LEVELS;g++){
                    normalized[c][g]*=g;
                }
            }
            for (int g=1;g<GRAY_LEVELS;++g){
                for (int c = 0; c < getChannels(); ++c) {
                    normalized[c][g] += normalized[c][g-1];
                }
            }
            return normalized;
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

		public Image getFullGrayscale() {
			final byte [][][] raw = new byte [3][getWidth()][getHeight()];
			if (getChannels() == 3) {
				for (int h = 0; h < getHeight(); ++h)
					for (int w = 0; w < getWidth(); ++w) {
						final int red = Byte.toUnsignedInt(rawRed()[w][h]);
						final int green = Byte.toUnsignedInt(rawGreen()[w][h]);
						final int blue = Byte.toUnsignedInt(rawBlue()[w][h]);
						final double brightness = Color.RGBtoHSB(red, green, blue, null)[BRIGHTNESS];
						raw[0][w][h] = (byte) ((GRAY_LEVELS - 1) * brightness);
						raw[1][w][h] = raw[0][w][h];
						raw[2][w][h] = raw[0][w][h];
					}
			}
			else {
				for (int h = 0; h < getHeight(); ++h)
					for (int w = 0; w < getWidth(); ++w) {
						raw[0][w][h] = rawGray()[w][h];
						raw[1][w][h] = raw[0][w][h];
						raw[2][w][h] = raw[0][w][h];
					}
			}
			return new Image(source, raw);
		}

		public Image getEqualized() {
			final byte [][][] raw = new byte [getChannels()][getWidth()][getHeight()];
			final double [][] cum = getCummulativeHistogram();
			final double [] cumMin = new double [getChannels()];
			for (int c = 0; c < getChannels(); ++c)
				for (int g = 0; g < GRAY_LEVELS; ++g) {
					if (0 < cum[c][g]) {
						cumMin[c] = cum[c][g];
						break;
					}
				}
			for (int h = 0; h < getHeight(); ++h)
				for (int w = 0; w < getWidth(); ++w)
					for (int c = 0; c < getChannels(); ++c) {
						final int gray = Byte.toUnsignedInt(rawData[c][w][h]);
						final double eqGray = (cum[c][gray] - cumMin[c]) / (1.0 - cumMin[c]);
						raw[c][w][h] = (byte) Math.floor(0.5 + eqGray * (GRAY_LEVELS - 1));
					}
			return new Image(source, raw);
		}

		public double[] getMean() {
		    double [] mean = new double[getChannels()];
		    for (int c=0;c<getChannels();c++) {
		        for (int w=0;w<getWidth();w++) {
		            for (int h=0;h<getHeight();h++){
		                mean[c]+=data[c][w][h];
                    }
                }
                mean[c]/=getHeight()*getWidth();
            }
            this.mean=mean;
            return mean;
        }

        public double[] getStdDev() {
		    double [] stdDev = new double[getChannels()];
		    for (int c=0;c<getChannels();c++) {
                for (int w=0;w<getWidth();w++) {
                    for (int h=0;h<getHeight();h++){
                        stdDev[c]+=Math.pow(data[c][w][h]-mean[c],2);
                    }
                }
                stdDev[c]/=getHeight()*getWidth();
                stdDev[c]=Math.sqrt(stdDev[c]);
            }
            //this.stdDev = stdDev;
            return stdDev;
        }
	}
