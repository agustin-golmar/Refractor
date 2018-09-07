
	package ar.nadezhda.refractor.handler.edge;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.handler.compression.CompressorService;
	import ar.nadezhda.refractor.handler.edge.operator.PrewittOperator;
	import ar.nadezhda.refractor.interfaces.DerivativeOperator;
	import ar.nadezhda.refractor.interfaces.Handler;
	import ar.nadezhda.refractor.support.Matrix;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.scene.Node;
	import javafx.scene.control.Alert.AlertType;

	public class PrewittHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final Node node) {
			final Map<String, Image> result = new HashMap<>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'copy' action.");
				return result;
			}
			final DerivativeOperator operator = new PrewittOperator();
			final CompressorService compressor = Main.context
					.getBean(CompressorService.class);
			final ImageState state = states.get(0);
			final Image image = state.getImage();
			final Image image_x = new Image(image.getSource(), operator.convolutionOverX(image.data));
			final Image image_y = new Image(image.getSource(), operator.convolutionOverY(image.data));
			final Image image_p = new Image(image.getSource(),
					compressor.compress(Matrix.absoluteGradient(image_x.data, image_y.data)));
			final String key_x = ImageTool.buildKey("prewitt_x", image_x, state.getKey());
			final String key_y = ImageTool.buildKey("prewitt_y", image_y, state.getKey());
			final String key_p = ImageTool.buildKey("prewitt", image_p, state.getKey());
			result.put(key_x, image_x);
			result.put(key_y, image_y);
			result.put(key_p, image_p);
			return result;
		}
	}
