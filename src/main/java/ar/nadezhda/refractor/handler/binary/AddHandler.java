
	package ar.nadezhda.refractor.handler.binary;

	public class AddHandler extends BinaryHandler {

		public AddHandler() {
			super("add", (l, r) -> l + r);
		}
	}
