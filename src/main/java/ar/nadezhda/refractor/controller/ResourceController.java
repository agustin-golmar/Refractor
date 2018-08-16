
	package ar.nadezhda.refractor.controller;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.core.Workspace;
	import java.io.File;
	import java.io.IOException;
	import java.text.DecimalFormat;
	import java.text.DecimalFormatSymbols;
	import java.util.Optional;
	import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
	import javafx.scene.control.CheckBox;
	import javafx.scene.control.Label;
	import javafx.scene.image.ImageView;
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
		protected final DecimalFormat decimal;

		@FXML protected CheckBox useConfigForLoad;
		@FXML protected Label mouseLocation;
		@FXML protected Label areaDimension;
		@FXML protected Label pixelCount;
		@FXML protected Label grayAverage;

		public ResourceController() {
			this.chooser = new FileChooser();
			this.chooser.setInitialDirectory(new File("res/image"));
			this.chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("All files", "*.*"),
					new FileChooser.ExtensionFilter("BMP", "*.bmp"),
					new FileChooser.ExtensionFilter("PGM", "*.pgm"),
					new FileChooser.ExtensionFilter("PPM", "*.ppm"),
					new FileChooser.ExtensionFilter("RAW", "*.raw"));
			final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			this.decimal = new DecimalFormat("0.000", symbols);
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
						final ImageView view = ImageTool.displayImage(wImage);
						augment(view, image);
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

		protected void augment(final ImageView view, final Image image) {
			view.setUserData(new ImageState(view, image));
			view.setOnMouseMoved(event -> {
				mouseLocation.setText("Location (x, y) = ("
						+ (int) event.getX() + ", " + (int) event.getY() + ")");
			});
			view.setOnMousePressed(event -> {
				((ImageState) view.getUserData())
					.setStartArea(event.getX(), event.getY());
			});
			view.setOnMouseReleased(event -> {
				final ImageState state = (ImageState) view.getUserData();
				state.setEndArea(event.getX(), event.getY());
				areaDimension.setText("Area (width, height) = ("
						+ state.getXArea() + ", " + state.getYArea() + ")");
				pixelCount.setText("Pixel Count: " + state.pixelCount());
				final double [] avg = state.getRGBAverageOnArea();
				grayAverage.setText("Average (R, G, B) = (" +
						decimal.format(avg[0]) + ", " +
						decimal.format(avg[1]) + ", " +
						decimal.format(avg[2]) + ")");
				state.resetArea();
			});
		}
	}
