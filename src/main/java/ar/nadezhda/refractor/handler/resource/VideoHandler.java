package ar.nadezhda.refractor.handler.resource;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class VideoHandler implements Handler {

	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		if (states.isEmpty()) return Collections.emptyMap();
		states.sort(Comparator.comparing(ImageState::getKey));
		final int fps = Integer.parseInt(((TextField) Main.namespace.get("fps")).getText());
		final double Δt = 1.0/fps;
		final ImageView view = new ImageView();
		final Stage stage = new Stage();
		final StackPane root = new StackPane();
		final var first = states.get(0).getImage();
		final Scene scene = new Scene(root, first.getWidth(), first.getHeight());
		root.setAlignment(Pos.TOP_LEFT);
		root.getChildren().add(view);
		stage.setScene(scene);
		stage.setTitle("Video at " + fps + " FPS.");
		stage.show();

		// Se ejecuta una vez por frame...
		new AnimationTimer() {

			private final ToggleButton paused = (ToggleButton) Main.namespace.get("paused");
			private final ToggleButton stopped = (ToggleButton) Main.namespace.get("stopped");
			private double last = 0.0;
			private int index = 0;

			@Override
			public void handle(final long now) {
				final double nowInSeconds = now * 1.0E-9;
				if (stopped.isSelected()) {
					paused.setSelected(false);
					stopped.setSelected(false);
					stop();
					return;
				}
				if (paused.isSelected()) return;
				if (Δt < nowInSeconds - last) {
					last = nowInSeconds;
					final var state = states.get(index);
					stage.setTitle("Video at " + fps + " FPS. Current: " + index);
					view.setImage(state.getView().getImage());
					++index;
					if (index == states.size()) {
						index = 0;
					}
				}
			}
		}.start();
		return Collections.emptyMap();
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
