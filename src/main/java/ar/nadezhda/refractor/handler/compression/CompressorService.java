
	package ar.nadezhda.refractor.handler.compression;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.interfaces.Compressor;
	import javafx.scene.control.RadioMenuItem;
	import javafx.scene.control.ToggleGroup;
	import java.util.Map;
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
		@SuppressWarnings("unchecked")
		public double [][][] compress(final double [][][] data) {
			final Map<String, Object> namespace = Main.context
					.getBean("namespace", Map.class);
			final RadioMenuItem item = (RadioMenuItem) ((ToggleGroup) namespace
					.get("compressor"))
					.getSelectedToggle();
			switch (item.getText()) {
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
	}
