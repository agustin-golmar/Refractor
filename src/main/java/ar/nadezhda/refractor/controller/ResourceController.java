
	package ar.nadezhda.refractor.controller;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.core.Workspace;
	import java.io.File;
	import java.io.IOException;
	import java.util.Optional;
	import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
	import javafx.scene.control.CheckBox;
	import javafx.scene.image.WritableImage;
	import javafx.stage.FileChooser;

		/**
		* <p>Controlador encargado de entrada y salida de imágenes.</p>
		*
		* @see <a href = "https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/file-chooser.htm">
		* 	File Chooser</a>
		* @see <a href = "http://tutorials.jenkov.com/javafx/fxml.html">Jenkov on FXML</a>
		*/

	public class ResourceController {

		protected final FileChooser chooser;

		@FXML protected CheckBox useConfigForLoad;

		public ResourceController() {
			this.chooser = new FileChooser();
			this.chooser.setInitialDirectory(new File("res/image"));
			this.chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("All files", "*.*"),
					new FileChooser.ExtensionFilter("BMP", "*.bmp"),
					new FileChooser.ExtensionFilter("PGM", "*.pgm"),
					new FileChooser.ExtensionFilter("PPM", "*.ppm"),
					new FileChooser.ExtensionFilter("RAW", "*.raw"));
		}

		@FXML
		protected void open(final ActionEvent event) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			chooser.setTitle("Refractor: Open an image...");
			Optional.ofNullable(chooser.showOpenDialog(Main.stage))
					.flatMap(file -> {
						try {
							final String path = file.getCanonicalPath();
							if (useConfigForLoad.isSelected()) {
								return workspace.loadImageUsingConfig(path);
							}
							else {
								return workspace.loadImage(path);
							}
						}
						catch (final IOException exception) {
							exception.printStackTrace();
							return Optional.empty();
						}
					})
					.ifPresentOrElse(image -> {
						final WritableImage wImage = ImageTool.getImageForDisplay(image);
						ImageTool.displayImage(wImage);
					}, () -> {
						System.out.println("No se pudo abrir la imagen.");
					});
		}

		@FXML
		protected void save(final ActionEvent event) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			chooser.setTitle("Refractor: Save an image...");
			Optional.ofNullable(chooser.showSaveDialog(Main.stage))
				.ifPresentOrElse(file -> {
					try {
						final String path = file.getCanonicalPath();
						workspace.saveImage(workspace.loadImage("res/image/lalala.bmp").get(), path);
						// Qué imagen almacena?
					}
					catch (final IOException exception) {
						exception.printStackTrace();
					}
				}, () -> {
					System.out.println("No se pudo guardar la imagen.");
				});
		}
	}
