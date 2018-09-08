
	package ar.nadezhda.refractor.handler.unary;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;

	public class EqualizerHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final Map<String, Image> result = new HashMap<>();
			states.stream()
				.forEachOrdered(state -> {
					final Image image = state.getImage().getEqualized();
					final String key = ImageTool.buildKey("equalized", image, state.getKey());
					result.put(key, image);
				});
			return result;
		}
	}
