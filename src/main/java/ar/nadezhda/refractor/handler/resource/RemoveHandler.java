
	package ar.nadezhda.refractor.handler.resource;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.core.Workspace;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.Collections;
	import java.util.List;
	import java.util.Map;
	import javafx.collections.ObservableList;
	import javafx.event.ActionEvent;
	import javafx.scene.Node;
	import javafx.scene.control.ListView;

	public class RemoveHandler implements Handler {

		@Override
		@SuppressWarnings("unchecked")
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final Workspace workspace = Main.context.getBean(Workspace.class);
			final Node node = (Node) action.getSource();
			final ObservableList<String> keys = ((ListView<String>) node
					.getScene()
					.lookup("#keys"))
					.getItems();
			states.forEach(state -> {
				ImageTool.closeImageView(state);
				workspace.removeState(state.getKey());
				keys.remove(state.getKey());
			});
			return Collections.emptyMap();
		}
	}
