
	package ar.nadezhda.refractor.handler.binary;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.function.DoubleBinaryOperator;
	import javafx.event.ActionEvent;
	import javafx.scene.control.Alert.AlertType;

	public class BinaryHandler implements Handler {

		protected final String action;
		protected final DoubleBinaryOperator operation;

		public BinaryHandler(final String action,
				final DoubleBinaryOperator operation) {
			this.action = action;
			this.operation = operation;
		}

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final Map<String, Image> result = new HashMap<>();
			if (states.size() != 2) {
				ImageTool.popup(AlertType.WARNING, "Warning!", new StringBuilder()
						.append("You must select exactly 2 images to apply the '")
						.append(this.action)
						.append("' action.")
						.toString());
				return result;
			}
			final Image image = states.get(0).operate(states.get(1), operation);
			final String key = ImageTool.buildKey(this.action, image,
					states.get(0).getKey(), states.get(1).getKey());
			result.put(key, image);
			return result;
		}
	}
