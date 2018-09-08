
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
	import javafx.event.ActionEvent;
	import javafx.scene.control.Alert.AlertType;

	public class GradientEdgeDetectorHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final var result = new HashMap<String, Image>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'edge detection'.");
				return result;
			}
			// Get from toggle:
			final DerivativeOperator operator = new PrewittOperator();
			final var compressor = Main.context.getBean(CompressorService.class);
			final var state = states.get(0);
			final var image = state.getImage();
			final var dx = operator.convolutionOverX(image.data);
			final var dy = operator.convolutionOverY(image.data);
			final var grad = Matrix.absoluteGradient(dx, dy);
			final var borders = new Image(image.getSource(), compressor.compress(grad));
			final var key = ImageTool.buildKey("gradient-edge", borders,
					operator.getClass().getSimpleName(), state.getKey());
			result.put(key, borders);
			return result;
		}
	}
