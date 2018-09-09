
	package ar.nadezhda.refractor.handler.binary;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.handler.compression.LinearCompressor;
	import ar.nadezhda.refractor.interfaces.Compressor;

	public class SubHandler extends BinaryHandler {

		public SubHandler() {
			super("sub", (l, r) -> l - r);
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(LinearCompressor.class);
		}
	}
