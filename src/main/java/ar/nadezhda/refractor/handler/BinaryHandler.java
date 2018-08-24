
	package ar.nadezhda.refractor.handler;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.function.DoubleBinaryOperator;
	import javafx.scene.Node;

	public class BinaryHandler implements Handler {

		protected final String action;
		protected final DoubleBinaryOperator operation;

		public BinaryHandler(final String action,
				final DoubleBinaryOperator operation) {
			this.action = action;
			this.operation = operation;
		}

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final Node node) {
			final Map<String, Image> result = new HashMap<>();
			if (states.size() != 2) {
				System.out.println("Solo 2 imágenes, ni 3 ni 1.");
				return result;
			}
			final Image image = states.get(0).operate(states.get(1), operation);
			final String key = ImageTool.buildKey(action,
					states.get(0).getKey() + ", " + states.get(1).getKey(), image);
			result.put(key, image);
			return result;
		}
	}
