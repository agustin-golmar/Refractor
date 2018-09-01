
	package ar.nadezhda.refractor.handler.edge;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.handler.compression.CompressorService;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.scene.Node;
	import javafx.scene.control.Alert.AlertType;

	public class PrewittHandler implements Handler {

		protected final double [][] PREWITT_MASK_X = {
			{-1, 0, 1},
			{-1, 0, 1},
			{-1, 0, 1}
		};

		protected final double [][] PREWITT_MASK_Y = {
			{-1, -1, -1},
			{0, 0, 0},
			{1, 1, 1}
		};

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final Node node) {
			final Map<String, Image> result = new HashMap<>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'copy' action.");
				return result;
			}
			final CompressorService compressor = Main.context
					.getBean(CompressorService.class);
			final ImageState state = states.get(0);
			final Image image_x = filter(state.getImage(), PREWITT_MASK_X);
			final Image image_y = filter(state.getImage(), PREWITT_MASK_Y);
			final Image image = new Image(
					state.getImage().getSource(),
					compressor.compress(absoluteGradient(image_x.data, image_y.data)));
			final String key_x = ImageTool.buildKey("prewitt_x", image_x, state.getKey());
			final String key_y = ImageTool.buildKey("prewitt_y", image_y, state.getKey());
			final String key = ImageTool.buildKey("prewitt", image, state.getKey());
			result.put(key_x, image_x);
			result.put(key_y, image_y);
			result.put(key, image);
			return result;
		}

		protected Image filter(final Image image, final double [][] mask) {
			final double [][][] data = image.getEmptyImageSpace();
			final int dim = mask.length;
			final int base = dim/2;
			// No toca los bordes!
			for (int h = base; h < image.getHeight() - base; ++h)
				for (int w = base; w < image.getWidth() - base; ++w)
					for (int c = 0; c < image.getChannels(); ++c) {
						double filtered = 0.0;
						for (int i = 0; i < dim; ++i)
							for (int j = 0; j < dim; ++j) {
								filtered += mask[i][j] * image.data[c][w - base + i][h - base + j];
							}
						data[c][w][h] = filtered;
					}
			return new Image(image.getSource(), data);
		}

		protected double [][][] absoluteGradient(final double [][][] dx, final double [][][] dy) {
			final double [][][] absGrad = new double [dx.length][dx[0].length][dx[0][0].length];
			for (int h = 0; h < dx[0][0].length; ++h)
				for (int w = 0; w < dx[0].length; ++w)
					for (int c = 0; c < dx.length; ++c) {
						absGrad[c][w][h] = Math.hypot(dx[c][w][h], dy[c][w][h]);
					}
			return absGrad;
		}
	}
