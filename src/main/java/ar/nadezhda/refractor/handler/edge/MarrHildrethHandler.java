
	package ar.nadezhda.refractor.handler.edge;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.handler.compression.LinearCompressor;
	import ar.nadezhda.refractor.interfaces.Compressor;
	import ar.nadezhda.refractor.interfaces.Handler;
	import ar.nadezhda.refractor.support.Matrix;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;
	import javafx.scene.control.Alert.AlertType;
	import javafx.scene.control.Slider;
	import javafx.scene.control.TextField;

	public class MarrHildrethHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final var result = new HashMap<String, Image>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'marr-hildreth detection'.");
				return result;
			}
			final var σ = ((Slider) Main.namespace.get("mhDevValue")).getValue();
			final var slope = ((Slider) Main.namespace.get("slopeValue")).getValue();
			final var dimText = ((TextField) Main.namespace.get("mhDimValue")).getText();
			final var dim = Integer.parseInt(dimText);
			final var state = states.get(0);
			final var image = state.getImage();
			final var conv = Matrix.convolution(image.data, Matrix.marrHildreth(dim, σ));
			final var roots = Matrix.roots(conv, slope);
			final var borders = new Image(image.getSource(), roots);
			final var key = ImageTool.buildKey("marr-hildreth-edge", borders, state.getKey());
			result.put(key, borders);
			return result;
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(LinearCompressor.class);
		}
	}
