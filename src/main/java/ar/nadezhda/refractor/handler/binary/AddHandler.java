
	package ar.nadezhda.refractor.handler.binary;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.handler.compression.LinearCompressor;
	import ar.nadezhda.refractor.interfaces.Compressor;

	public class AddHandler extends BinaryHandler {

		public AddHandler() {
			super("add", (l, r) -> l + r);
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(LinearCompressor.class);
		}
	}
