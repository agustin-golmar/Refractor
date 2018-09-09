
	package ar.nadezhda.refractor.handler.binary;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.handler.compression.DynamicRangeCompressor;
	import ar.nadezhda.refractor.interfaces.Compressor;

	public class ProductHandler extends BinaryHandler {

		public ProductHandler() {
			super("product", (l, r) -> l * r);
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(DynamicRangeCompressor.class);
		}
	}
