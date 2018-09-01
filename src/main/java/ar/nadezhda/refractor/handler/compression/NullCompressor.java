
	package ar.nadezhda.refractor.handler.compression;

	import ar.nadezhda.refractor.interfaces.Compressor;
	import org.springframework.stereotype.Component;

	@Component
	public class NullCompressor implements Compressor {

		@Override
		public double [][][] compress(final double [][][] data) {
			return data;
		}
	}
