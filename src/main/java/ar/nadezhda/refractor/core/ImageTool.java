
	package ar.nadezhda.refractor.core;

	import java.awt.image.BufferedImage;

	import javafx.scene.Scene;
	import javafx.scene.image.ImageView;
	import javafx.scene.image.PixelWriter;
	import javafx.scene.image.WritableImage;
	import javafx.scene.layout.StackPane;
	import javafx.scene.paint.Color;
	import javafx.stage.Stage;

	public class ImageTool {

		public static byte [][][] rawToImageMatrix(
				final byte [] data, final int offset,
				final int channels, final int width, final int height) {
			final byte [][][] image = new byte [channels][width][height];
			int k = 0;
			for (int h = 0; h < height; ++h)
				for (int w = 0; w < width; ++w)
					for (int c = 0; c < channels; ++c) {
						image[channels - c - 1][w][h] = data[offset + k];
						++k;
					}
			return image;
		}

		public static byte [][][] rawToImageMatrix(final BufferedImage buffer) {
			final int width = buffer.getWidth();
			final int height = buffer.getHeight();
			final byte [][][] image = new byte [3][width][height];
			for (int h = 0; h < buffer.getHeight(); ++h)
				for (int w = 0; w < buffer.getWidth(); ++w) {
					final int rgb = buffer.getRGB(w, h);
					image[0][w][h] = (byte) (rgb >> 16 & 0xFF);
					image[1][w][h] = (byte) (rgb >> 8 & 0xFF);
					image[2][w][h] = (byte) (rgb & 0xFF);
				}
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

		public static Color rawRGB(final Image image, final int x, final int y) {
			if (image.getChannels() == 1) {
				return Color.grayRgb(Byte.toUnsignedInt(image.rawGray()[x][y]));
			}
			else if (image.getChannels() == 3) {
				return Color.rgb(
					Byte.toUnsignedInt(image.rawRed()[x][y]),
					Byte.toUnsignedInt(image.rawGreen()[x][y]),
					Byte.toUnsignedInt(image.rawBlue()[x][y]));
			}
			else return Color.BLACK;
		}

		public static WritableImage getImageForDisplay(final Image image) {
			final WritableImage wImage
				= new WritableImage(image.getWidth(), image.getHeight());
			final PixelWriter pixel = wImage.getPixelWriter();
			for (int h = 0; h < image.getHeight(); ++h)
				for (int w = 0; w < image.getWidth(); ++w) {
					pixel.setColor(w, h, rawRGB(image, w, h));
				}
			return wImage;
		}

		public static ImageView displayImage(final WritableImage image) {
			final ImageView view = new ImageView();
			final Stage stage = new Stage();
			final StackPane root = new StackPane();
			final Scene scene
				= new Scene(root, image.getWidth(), image.getHeight());
			root.getChildren().add(view);
			stage.setScene(scene);
			view.setImage(image);
			stage.show();
			return view;
		}

		public static int ARGB(final Image image, final int w, final int h) {
			final int red = Byte.toUnsignedInt(image.rawRed()[w][h]);
			final int green = Byte.toUnsignedInt(image.rawGreen()[w][h]);
			final int blue = Byte.toUnsignedInt(image.rawBlue()[w][h]);
			return (red << 16) | (green << 8) | (blue);
		}
	}
