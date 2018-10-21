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
import javafx.scene.control.Alert.AlertType;

public class SUSANHandler implements Handler {

	protected static final double [][] kernel = {
		{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0},
		{0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0},
		{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
		{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
		{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
		{0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0},
		{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0}
	};

	protected static final double threshold = 27;
	protected static final double N = 37.0;

	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		final var result = new HashMap<String, Image>();
		if (states.size() != 1) {
			ImageTool.popup(AlertType.WARNING, "Warning!",
				"You must select only 1 image to apply the 'S.U.S.A.N. Detector'.");
		}
		else {
			final var state = states.get(0);
			final var image = state.getImage().getFullGrayscale();
			final var count = Matrix.convolution(image.data, kernel, (c, w, h, k, s) -> {
				return (0.0 < k && Math.abs(image.data[c][w][h] - s) < threshold)? 1.0 : 0.0;
			});
			final var corners = Matrix.filter(count, (space, c, w, h) -> {
				final double s = 1.0 - space[c][w][h]/N;
				if (0.4 < s && s < 0.6) return Matrix.BORDER;
				if (0.6 < s && s < 0.8) return Matrix.CORNER;
				return Matrix.NOTHING;
			});
			final var map = new Image(image.getSource(), Matrix.colorFeatures(corners, image.data));
			final var key = ImageTool.buildKey("susan-detector", map, state.getKey());
			result.put(key, map);
		}
		return result;
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
