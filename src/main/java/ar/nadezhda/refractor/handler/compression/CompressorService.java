
	package ar.nadezhda.refractor.handler.compression;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.interfaces.Compressor;
	import javafx.scene.control.RadioMenuItem;
	import javafx.scene.control.ToggleGroup;
	import javax.inject.Inject;
	import org.springframework.stereotype.Service;

	@Service
	public class CompressorService implements Compressor {

		protected final DynamicRangeCompressor dynamicRange;
		protected final LinearCompressor linear;
		protected final TruncatedCompressor truncated;
		protected final NullCompressor nullCompressor;

		@Inject
		public CompressorService(
				final DynamicRangeCompressor dynamicRange,
				final LinearCompressor linear,
				final TruncatedCompressor truncated,
				final NullCompressor nullCompressor) {
			this.dynamicRange = dynamicRange;
			this.linear = linear;
			this.truncated = truncated;
			this.nullCompressor = nullCompressor;
		}

		@Override
		public double [][][] compress(final double [][][] data) {
			final RadioMenuItem item = (RadioMenuItem) ((ToggleGroup) Main.namespace
					.get("compressor"))
					.getSelectedToggle();
			switch (item.getText()) {
				case "Automatic":
					return data;
				case "Dynamic-range":
					return dynamicRange.compress(data);
				case "Linear":
					return linear.compress(data);
				case "Truncated":
					return truncated.compress(data);
				default:
					return nullCompressor.compress(data);
			}
		}

		public boolean isAutomatic() {
			return ((RadioMenuItem) ((ToggleGroup) Main.namespace
					.get("compressor"))
					.getSelectedToggle())
					.getText()
					.equals("Automatic");
		}
	}
