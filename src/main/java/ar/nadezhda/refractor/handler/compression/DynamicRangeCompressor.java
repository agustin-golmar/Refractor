
	package ar.nadezhda.refractor.handler.compression;

	import ar.nadezhda.refractor.interfaces.Compressor;
	import org.springframework.stereotype.Component;

	@Component
	public class DynamicRangeCompressor implements Compressor {

		@Override
		public double [][][] compress(final double [][][] data) {
			double min = data[0][0][0];
			double max = min;
			for (int h = 0; h < data[0][0].length; ++h)
				for (int w = 0; w < data[0].length; ++w)
					for (int c = 0; c < data.length; ++c) {
						min = Math.min(min, data[c][w][h]);
						max = Math.max(max, data[c][w][h]);
					}
			final double C = 255.0 / Math.log(1.0 + max - min);
			for (int h = 0; h < data[0][0].length; ++h)
				for (int w = 0; w < data[0].length; ++w)
					for (int c = 0; c < data.length; ++c) {
						data[c][w][h] = C * Math.log(1.0 + data[c][w][h] - min);
					}
			return data;
		}
	}
