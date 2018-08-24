
	package ar.nadezhda.refractor.handler;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.scene.Node;

	public class CopyHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final Node node) {
			final Map<String, Image> result = new HashMap<>();
			if (states.size() != 1) {
				System.out.println("Seleccione solo una imagen. No más.");
				return result;
			}
			final ImageState state = states.get(0);
			if (0 < state.getPixelCount()) {
				final Image image = state.getSelectedImage();
				final String key = ImageTool.buildKey("copy", state.getKey(), image);
				result.put(key, image);
			}
			else {
				System.out.println("El área seleccionada no contiene píxeles.");
			}
			return result;
		}
	}
