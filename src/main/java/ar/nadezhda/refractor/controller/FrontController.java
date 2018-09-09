
	package ar.nadezhda.refractor.controller;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.core.Workspace;
	import ar.nadezhda.refractor.interfaces.Controller;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.net.URL;
	import java.text.DecimalFormat;
	import java.text.DecimalFormatSymbols;
	import java.util.Collections;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import java.util.ResourceBundle;
	import java.util.stream.Collectors;
	import javafx.collections.ObservableList;
	import javafx.css.Styleable;
	import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
	import javafx.fxml.Initializable;
    import javafx.scene.control.*;
    import javafx.scene.image.ImageView;
    import javafx.scene.image.WritableImage;

	public class FrontController
		implements Controller, Initializable {

		@FXML protected ListView<String> keys;
		@FXML protected CheckMenuItem reverseImageOrder;
		@FXML protected Label mouseLocation;
		@FXML protected Label areaDimension;
		@FXML protected Label pixelCount;
		@FXML protected Label grayAverage;

		protected final Map<String, Handler> router;
		protected final Workspace workspace;
		protected final DecimalFormat decimal;

		@SuppressWarnings("unchecked")
		public FrontController() {
			final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			this.router = (Map<String, Handler>) Main.context.getBean("router");
			this.workspace = Main.context.getBean(Workspace.class);
			this.decimal = new DecimalFormat("0.000", symbols);
		}

		@Override @FXML
		public void control(final ActionEvent event) {
			final Styleable node = (Styleable) event.getSource();
			Optional.ofNullable(router.get(node.getId()))
				.ifPresent(handler -> {
					final List<ImageState> states = getSelectedStates();
					if (reverseImageOrder.isSelected()) {
						Collections.reverse(states);
					}
					handler.handle(states, event)
						.forEach((key, image) -> {
							addImage(key, image);
						});
				});
		}

		@Override
		public void initialize(final URL url, final ResourceBundle resources) {
			keys.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}

		protected ObservableList<String> getSelectedKeys() {
			return keys.getSelectionModel().getSelectedItems();
		}

		protected List<ImageState> getSelectedStates() {
			return getSelectedKeys().stream()
				.map(workspace::getState)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		}

		protected ImageState addImage(final String key, final Image image) {
			final WritableImage wImage = ImageTool.getImageForDisplay(image);
			final ImageState state = ImageTool.displayNewImage(key, wImage, image);
			addTriggers(state);
			keys.getItems().add(key);
			workspace.addState(key, state);
			return state;
		}

		protected ImageState addTriggers(final ImageState state) {
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
				pixelCount.setText("Pixel Count: " + state.getPixelCount());
				final double [] avg = state.getRGBAverageOnArea();
				grayAverage.setText("Average (R, G, B) = (" +
						decimal.format(avg[0]) + ", " +
						decimal.format(avg[1]) + ", " +
						decimal.format(avg[2]) + ")");
			});
			return state;
		}
	}
