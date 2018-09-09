
	package ar.nadezhda.refractor.interfaces;

	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;

	public interface Handler {

		/**
		* <p>Aplica alguna transformación sobre los estados de las imágenes
		* recibidas. La transformación puede o no generar nuevos estados.</p>
		*
		* @param states
		*	La lista de estados de imágenes seleccionadas. Sin ningún orden en
		*	particular.
		* @param action
		*	La acción que activó el llamado a este handler (<i>i.e.</i>, un
		*	botón, un slider, etc.).
		*
		* @return
		*	Un mapa con las imágenes generadas y las claves bajo las cuáles se
		*	deben persistir.
		*/
        public Map<String, Image> handle(
                final List<ImageState> states, final ActionEvent action);

        /**
        * <p>Devuelve el compresor por defecto para la operación realizada,
        * el cual será utilizado si el modo de compresión seleccionado es el
        * <b>automático</b>.</p>
        *
        * @return
        *	El compresor recomendado.
        */
        public Compressor getCompressor();
	}
