
	package ar.nadezhda.refractor.handler.resource;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.core.Workspace;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
	import java.io.File;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import java.util.function.Function;
	import java.util.stream.Collectors;
	import javafx.event.ActionEvent;
	import javafx.scene.control.Alert.AlertType;
	import javafx.stage.FileChooser;
	import javafx.stage.FileChooser.ExtensionFilter;

	public class OpenRAWHandler implements Handler {

		protected final FileChooser chooser;

		public OpenRAWHandler() {
			this.chooser = new FileChooser();
			this.chooser.setInitialDirectory(new File("."));
			this.chooser.setTitle("Refractor: Open an image...");
			// Only RAW filter...
			this.chooser.getExtensionFilters()
				.add(((ExtensionFilter []) Main.context.getBean("filters"))[7]);
		}

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			final Map<String, Image> result = new HashMap<>();
			Optional.ofNullable(chooser.showOpenMultipleDialog(Main.stage))
				.ifPresent(files -> {
					result.putAll(files.stream()
						.map(file -> {
							try {
								final String path = file.getCanonicalPath();
								if (workspace.getState(path).isPresent()) {
									ImageTool.popup(AlertType.INFORMATION, "Information",
										"The image is already in the workspace.");
								}
								else {
									return workspace.loadImageUsingConfig(path);
								}
							}
							catch (final IOException exception) {
								exception.printStackTrace();
							}
							return Optional.<Image>empty();
						})
						.filter(Optional::isPresent)
						.map(Optional::get)
						.collect(Collectors.toMap(Image::getSource, Function.identity())));
				});
			return result;
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(NullCompressor.class);
		}
	}
