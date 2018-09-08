
	package ar.nadezhda.refractor.handler;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;
	import javafx.scene.control.Alert.AlertType;

	public class CopyHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final Map<String, Image> result = new HashMap<>();
			if (states.size() != 1) {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"You must select only 1 image to apply the 'copy' action.");
				return result;
			}
			final ImageState state = states.get(0);
			if (0 < state.getPixelCount()) {
				final Image image = state.getSelectedImage();
				final String key = ImageTool.buildKey("copy", image, state.getKey());
				result.put(key, image);
			}
			else {
				ImageTool.popup(AlertType.WARNING, "Warning!",
					"The selected area has no pixels.");
			}
			return result;
		}
	}
