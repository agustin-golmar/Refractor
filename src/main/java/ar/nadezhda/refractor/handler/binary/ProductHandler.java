
	package ar.nadezhda.refractor.handler.binary;

	public class ProductHandler extends BinaryHandler {

		public ProductHandler() {
			super("product", (l, r) -> l * r);
		}
	}
