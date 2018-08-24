
	package ar.nadezhda.refractor.handler;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.Workspace;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.io.File;
	import java.io.IOException;
	import java.util.Collections;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import javafx.scene.Node;
	import javafx.stage.FileChooser;
	import javafx.stage.FileChooser.ExtensionFilter;

	public class SaveHandler implements Handler {

		protected final FileChooser chooser;

		public SaveHandler() {
			this.chooser = new FileChooser();
			this.chooser.setInitialDirectory(new File("res/image")); // Dejar en '.'
			this.chooser.setTitle("Refractor: Save an image...");
			this.chooser.getExtensionFilters()
				.addAll((ExtensionFilter []) Main.context.getBean("filters"));
		}

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final Node node) {
			if (states.size() != 1) {
				System.out.println("Seleccione solo una imagen. No más.");
				return Collections.emptyMap();
			}
			Optional.ofNullable(chooser.showSaveDialog(Main.stage))
				.ifPresentOrElse(file -> {
					try {
						final String path = file.getCanonicalPath();
						Main.context.getBean(Workspace.class)
							.saveImage(states.get(0).getImage(), path);
					}
					catch (final IOException exception) {
						exception.printStackTrace();
					}
				}, () -> {
					System.out.println("No se pudo guardar la imagen.");
				});
			return Collections.emptyMap();
		}
	}
