
	package ar.nadezhda.refractor.interfaces;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import java.util.List;
	import java.util.Map;
	import javafx.scene.Node;

	@FunctionalInterface
	public interface Handler {

		/**
		* <p>Aplica alguna transformación sobre los estados de las imágenes
		* recibidas. La transformación puede o no generar nuevos estados.</p>
		*
		* @param states
		*	La lista de estados de imágenes seleccionadas. Sin ningún orden en
		*	particular.
		* @param node
		*	El nodo de la escena que activó el llamado a este handler
		*	(<i>i.e.</i>, un botón, un slider, etc.).
		*
		* @return
		*	Un mapa con las imágenes generadas y las claves bajo las cuáles se
		*	deben persistir.
		*/
        Map<String, Image> handle(
                final List<ImageState> states, final Node node);
	}
