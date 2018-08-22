
	package ar.nadezhda.refractor.controller;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.core.Workspace;
	import java.io.File;
	import java.io.IOException;
	import java.net.URL;
	import java.text.DecimalFormat;
	import java.text.DecimalFormatSymbols;
	import java.util.Optional;
	import java.util.ResourceBundle;
	import javafx.collections.ObservableList;
	import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
	import javafx.fxml.Initializable;
	import javafx.scene.control.CheckBox;
	import javafx.scene.control.Label;
	import javafx.scene.control.ListView;
	import javafx.scene.control.SelectionMode;
	import javafx.scene.image.ImageView;
	import javafx.scene.image.WritableImage;
	import javafx.stage.FileChooser;

		/**
		* <p>Controlador encargado de entrada y salida de imágenes.</p>
		*/

	public class ResourceController implements Initializable {

		protected final FileChooser chooser;
		protected final DecimalFormat decimal;

		@FXML protected ListView<String> openImages;
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
					new FileChooser.ExtensionFilter("GIF", "*.gif"),
					new FileChooser.ExtensionFilter("JPG", "*.jpg"),
					new FileChooser.ExtensionFilter("PGM", "*.pgm"),
					new FileChooser.ExtensionFilter("PNG", "*.png"),
					new FileChooser.ExtensionFilter("PPM", "*.ppm"),
					new FileChooser.ExtensionFilter("RAW", "*.raw"),
					new FileChooser.ExtensionFilter("TIF", "*.tif"),
					new FileChooser.ExtensionFilter("WBMP", "*.wbmp"));
			final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			this.decimal = new DecimalFormat("0.000", symbols);
		}

		@Override
		public void initialize(final URL url, final ResourceBundle resources) {
			openImages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}

		@FXML
		protected void open(final ActionEvent event) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			chooser.setTitle("Refractor: Open an image...");
			Optional.ofNullable(chooser.showOpenMultipleDialog(Main.stage))
				.ifPresent(files -> files.stream()
					.map(file -> {
						try {
							final String path = file.getCanonicalPath();
							if (workspace.getState(path).isPresent()) {
								System.out.println("La imagen ya se encuentra en el workspace!");
							}
							else if (useConfigForLoad.isSelected()) {
								return workspace.loadImageUsingConfig(path);
							}
							else {
								return workspace.loadImage(path);
							}
						}
						catch (final IOException exception) {
							exception.printStackTrace();
						}
						return Optional.<Image>empty();
					})
					.filter(Optional::isPresent)
					.map(Optional::get)
					.forEach(image -> {
						final WritableImage wImage = ImageTool.getImageForDisplay(image);
						final ImageState state = ImageTool.displayNewImage(wImage, image);
						augment(state);
						openImages.getItems().add(image.getSource());
						workspace.addState(image.getSource(), state);
					}));
		}

		@FXML
		protected void save(final ActionEvent event) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			final ObservableList<String> selectedImages = openImages
					.getSelectionModel()
					.getSelectedItems();
			if (selectedImages.size() == 1) {
				System.out.println("Seleccionó: " + selectedImages.get(0));
				chooser.setTitle("Refractor: Save an image...");
				Optional.ofNullable(chooser.showSaveDialog(Main.stage))
					.ifPresentOrElse(file -> {
						try {
							final String path = file.getCanonicalPath();
							workspace.saveImage(workspace
									.getState(selectedImages.get(0)).get()
									.getImage(), path);
						}
						catch (final IOException exception) {
							exception.printStackTrace();
						}
					}, () -> {
						System.out.println("No se pudo guardar la imagen.");
					});
			}
			else if (selectedImages.size() == 0) {
				System.out.println("No ha seleccionado ninguna imagen para guardar.");
			}
			else {
				System.out.println("Seleccione solo una imagen. No más.");
			}
		}

		@FXML
		protected void remove(final ActionEvent event) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			final ObservableList<String> selected = openImages
					.getSelectionModel()
					.getSelectedItems();
			selected.forEach(key -> {
				ImageTool.closeImageView(workspace
						.getState(key).get());
				workspace.removeState(key);
			});
			openImages.getItems().removeAll(selected);
		}

		@FXML
		protected void display(final ActionEvent event) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			openImages
					.getSelectionModel()
					.getSelectedItems()
					.stream()
					.map(workspace::getState)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.forEach(state -> {
						ImageTool.closeImageView(state);
						ImageTool.displayImageView(state);
					});
		}

		protected ImageState augment(final ImageState state) {
			final ImageView view = state.getView();
			view.setOnMouseMoved(event -> {
				mouseLocation.setText("Location (x, y) = ("
						+ (int) event.getX() + ", " + (int) event.getY() + ")");
			});
			view.setOnMousePressed(event -> {
				((ImageState) view.getUserData())
					.setStartArea(event.getX(), event.getY());
			});
			view.setOnMouseDragged(event -> {
				((ImageState) view.getUserData())
					.updateArea(event.getX(), event.getY());
			});
			view.setOnMouseReleased(event -> {
				state.updateArea(event.getX(), event.getY());
				areaDimension.setText("Area (width, height) = ("
						+ state.getXArea() + ", " + state.getYArea() + ")");
				pixelCount.setText("Pixel Count: " + state.pixelCount());
				final double [] avg = state.getRGBAverageOnArea();
				grayAverage.setText("Average (R, G, B) = (" +
						decimal.format(avg[0]) + ", " +
						decimal.format(avg[1]) + ", " +
						decimal.format(avg[2]) + ")");
			});
			return state;
		}
	}
