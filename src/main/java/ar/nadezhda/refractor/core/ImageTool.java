
	package ar.nadezhda.refractor.core;

	import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.support.Tool;
	import java.awt.image.BufferedImage;
	import java.util.stream.Collectors;
	import java.util.stream.Stream;
	import javafx.geometry.Pos;
	import javafx.scene.Node;
	import javafx.scene.Scene;
	import javafx.scene.control.Alert;
	import javafx.scene.control.Alert.AlertType;
	import javafx.scene.image.ImageView;
	import javafx.scene.image.PixelWriter;
	import javafx.scene.image.WritableImage;
	import javafx.scene.layout.StackPane;
	import javafx.scene.paint.Color;
	import javafx.stage.Stage;

	public class ImageTool {

		// For UUIs (Universal Unique Identifiers):
		protected static int id = 0;

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

		public static void display(final Node node, final String title,
				final int width, final int height) {
			final Stage stage = new Stage();
			final StackPane root = new StackPane();
			final Scene scene = new Scene(root, width, height);
			root.setAlignment(Pos.TOP_LEFT);
			root.getChildren().add(node);
			stage.setScene(scene);
			stage.setTitle(title);
			stage.show();
		}

		public static ImageState displayNewImage(final String key,
				final WritableImage wImage, final Image image) {
			final ImageView view = new ImageView(wImage);
			final String title = new StringBuilder()
					.append(key)
					.append(" : ")
					.append(image.getWidth()).append("x").append(image.getHeight())
					.toString();
			display(view, title, image.getWidth(), image.getHeight());
			final ImageState state = new ImageState(key, view, image);
			view.setUserData(state);
			return state;
		}

		public static ImageState displayImageView(final ImageState state) {
			final Image image = state.getImage();
			final Stage stage = new Stage();
			final String title = new StringBuilder()
					.append(state.getKey())
					.append(" : ")
					.append(image.getWidth()).append("x").append(image.getHeight())
					.toString();
			stage.setScene(state.getRoot().getScene());
			stage.setTitle(title);
			stage.show();
			return state;
		}

		public static ImageState closeImageView(final ImageState state) {
			((Stage) state.getView().getScene().getWindow()).close();
			return state;
		}

		public static int ARGB(final Image image, final int w, final int h) {
			final int red = Byte.toUnsignedInt(image.rawRed()[w][h]);
			final int green = Byte.toUnsignedInt(image.rawGreen()[w][h]);
			final int blue = Byte.toUnsignedInt(image.rawBlue()[w][h]);
			return (red << 16) | (green << 8) | (blue);
		}

		public static String buildKey(final String action, final Image result,
				final String ... srcImageKeys) {
			final String srcs = Stream.of(srcImageKeys)
					.map(Tool::getFilename)
					.map(Tool::getUUI)
					.collect(Collectors.joining(", "));
			return new StringBuilder()
					.append("[").append(id++).append("] = ")
					.append(action)
					.append("(").append(srcs).append(")")
					.toString();
		}

		public static void popup(final AlertType type,
				final String header, final String message) {
			final Alert alert = new Alert(type);
			alert.setTitle(Main.config.getTitle());
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.showAndWait();
		}
	}
