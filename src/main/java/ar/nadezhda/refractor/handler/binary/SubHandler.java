
	package ar.nadezhda.refractor.handler.binary;

	public class SubHandler extends BinaryHandler {

		public SubHandler() {
			super("sub", (l, r) -> l - r);
		}
	}
