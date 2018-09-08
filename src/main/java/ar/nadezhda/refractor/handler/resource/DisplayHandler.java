
	package ar.nadezhda.refractor.handler.resource;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.Collections;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;

	public class DisplayHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			states.forEach(state -> {
				ImageTool.closeImageView(state);
				ImageTool.displayImageView(state);
			});
			return Collections.emptyMap();
		}
	}
