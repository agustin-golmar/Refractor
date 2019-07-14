package ar.nadezhda.refractor.handler;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;

public class ResetHandler implements Handler {

	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		states.stream().forEach(ImageState::resetAreas);
		return Collections.emptyMap();
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
