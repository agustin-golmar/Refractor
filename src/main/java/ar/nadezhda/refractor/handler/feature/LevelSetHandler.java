package ar.nadezhda.refractor.handler.feature;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import ar.nadezhda.refractor.support.Matrix;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;

public class LevelSetHandler implements Handler {

	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		final var result = new HashMap<String, Image>();
		states.stream()
			.forEachOrdered(state -> { // Estimar el tiempo de procesamiento...
				final var image = state.getImage();
				final var contour = image.data; // Cambiar...
				final var map = new Image(image.getSource(), Matrix.colorFeatures(contour, image.data));
				final var key = ImageTool.buildKey("level-set", map, state.getKey());
				result.put(key, map);
			});
		return result;
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
