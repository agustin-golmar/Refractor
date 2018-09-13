
	package ar.nadezhda.refractor.handler.compression;

	import ar.nadezhda.refractor.interfaces.Compressor;
	import org.springframework.stereotype.Component;

	@Component
	public class TruncatedCompressor implements Compressor {

		@Override
		public double [][][] compress(final double [][][] data) {
			for (int h = 0; h < data[0][0].length; ++h)
				for (int w = 0; w < data[0].length; ++w)
					for (int c = 0; c < data.length; ++c) {
						if (data[c][w][h] < 0.0) data[c][w][h] = 0.0;
						else if (255.0 < data[c][w][h]) data[c][w][h] = 255.0;
						else {}
                    }
			return data;
		}
	}
