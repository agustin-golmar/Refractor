
	package ar.nadezhda.refractor.controller;

	import ar.nadezhda.refractor.interfaces.Controller;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.HashMap;
	import java.util.Map;
	import java.util.Optional;
	import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
	import javafx.scene.Node;

	public class FrontController extends ResourceController implements Controller {

		protected final Map<String, Handler> router;

		public FrontController() {
			this.router = new HashMap<>();
		}

		@Override @FXML
		public void control(final ActionEvent event) {
			final Node node = (Node) event.getSource();
			System.out.println("ID: " + node.getId());
			Optional.ofNullable(router.get(node.getId()))
				.ifPresent(handler -> {
					handler.handle();
				});
		}
	}
