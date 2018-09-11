
	package ar.nadezhda.refractor.handler.edge;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.handler.compression.LinearCompressor;
	import ar.nadezhda.refractor.handler.edge.operator.LaplaceOperator;
	import ar.nadezhda.refractor.interfaces.Compressor;
	import ar.nadezhda.refractor.interfaces.Handler;
	import ar.nadezhda.refractor.support.Matrix;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;
	import javafx.scene.control.Alert.AlertType;
	import javafx.scene.control.Slider;

	public class LaplacianHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final var result = new HashMap<String, Image>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'laplacian detection'.");
				return result;
			}
			final var slope = ((Slider) Main.namespace.get("slopeValue")).getValue();
			final var state = states.get(0);
			final var image = state.getImage();
			final var laplacian = Matrix.convolution(image.data, new LaplaceOperator().dX());
			final var roots = Matrix.roots(laplacian, slope);
			final var borders = new Image(image.getSource(), roots);
			final var key = ImageTool.buildKey("laplacian-edge", borders, state.getKey());
			result.put(key, borders);
			return result;
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(LinearCompressor.class);
		}
	}
