
	package ar.nadezhda.refractor.handler;

	public class ProductHandler extends BinaryHandler {

		public ProductHandler() {
			super("product", (l, r) -> l * r);
		}
	}
