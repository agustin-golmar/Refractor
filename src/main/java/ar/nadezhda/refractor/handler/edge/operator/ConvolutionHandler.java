
	package ar.nadezhda.refractor.handler.edge.operator;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.handler.compression.LinearCompressor;
	import ar.nadezhda.refractor.interfaces.Compressor;
	import ar.nadezhda.refractor.interfaces.DerivativeOperator;
	import ar.nadezhda.refractor.interfaces.Handler;
	import javafx.event.ActionEvent;
	import javafx.scene.control.RadioButton;
	import javafx.scene.control.ToggleGroup;
	import javafx.scene.control.Alert.AlertType;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	public class ConvolutionHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final var result = new HashMap<String, Image>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'convolution'.");
				return result;
			}
			final var operator = getOperator();
			final var state = states.get(0);
			final var image = state.getImage();
			double [][][] conv;
			switch (getOption("direction")) {
				case "X (horizontal)":
					conv = operator.convolutionOverX(image.data);
					break;
				case "Y (vertical)":
					conv = operator.convolutionOverY(image.data);
					break;
				case "SW (south-west)":
					conv = operator.convolutionOverSW(image.data);
					break;
				case "SE (south-east)":
					conv = operator.convolutionOverSE(image.data);
					break;
				default: return null;
			}
			final var borders = new Image(image.getSource(), conv);
			final var key = ImageTool.buildKey("convolution", borders,
					operator.getClass().getSimpleName(), state.getKey());
			result.put(key, borders);
			return result;
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(LinearCompressor.class);
		}

		public static DerivativeOperator getOperator() {
			switch (getOption("directionalOperator")) {
				case "Prewitt":
					return new PrewittOperator();
				case "Sobel":
					return new SobelOperator();
				case "Kirsh":
					return new KirshOperator();
				case "Laplace":
					return new LaplaceOperator();
				case "Custom":
					return new CustomOperator();
				default: return null;
			}
		}

		public static String getOption(final String toggleGroup) {
			final RadioButton item = (RadioButton) ((ToggleGroup) Main.namespace
					.get(toggleGroup))
					.getSelectedToggle();
			return item.getText();
		}
	}
