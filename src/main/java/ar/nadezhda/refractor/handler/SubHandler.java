
	package ar.nadezhda.refractor.handler;

	public class SubHandler extends BinaryHandler {

		public SubHandler() {
			super("sub", (l, r) -> l - r);
		}
	}
