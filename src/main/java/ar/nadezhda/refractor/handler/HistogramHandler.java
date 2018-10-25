
	package ar.nadezhda.refractor.handler;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.core.Image;
	import ar.nadezhda.refractor.core.ImageState;
	import ar.nadezhda.refractor.core.ImageTool;
	import ar.nadezhda.refractor.handler.compression.NullCompressor;
	import ar.nadezhda.refractor.interfaces.Compressor;
	import ar.nadezhda.refractor.interfaces.Handler;
	import java.util.Collections;
	import java.util.List;
	import java.util.Map;
	import javafx.event.ActionEvent;
	import javafx.scene.chart.BarChart;
	import javafx.scene.chart.CategoryAxis;
	import javafx.scene.chart.NumberAxis;
	import javafx.scene.chart.XYChart.Data;
	import javafx.scene.chart.XYChart.Series;
	import javafx.scene.control.CheckMenuItem;

	public class HistogramHandler implements Handler {

		@Override
		public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
			final var normalize = (CheckMenuItem) Main.namespace
					.get("normalizeHistogram");
			states.stream()
				.forEachOrdered(state -> {
					final Image image = state.getImage();
					final BarChart<String, Number> histogram = buildHistogram(
							image, normalize.isSelected());
					final String title = new StringBuilder()
							.append("Histogram (")
							.append(state.getKey())
							.append(")")
							.toString();
					ImageTool.display(histogram, title, 768, 512, true);
				});
			return Collections.emptyMap();
		}

		@Override
		public Compressor getCompressor() {
			return Main.context.getBean(NullCompressor.class);
		}

		protected BarChart<String, Number> buildHistogram(
				final Image image, final boolean normalize) {
			final CategoryAxis xAxis = new CategoryAxis();
			final NumberAxis yAxis = new NumberAxis();
			final BarChart<String, Number> histogram = new BarChart<>(xAxis, yAxis);
			final Series<String, Number> series = new Series<>();
			final int [][] rawHistogram = image.getRawHistogram();
			final double factor = normalize? image.getSize() : 1.0;
			xAxis.setStartMargin(0);
			xAxis.setLabel("Gray Level");
			yAxis.setLabel("Frecuency");
			histogram.getData().add(series);
			histogram.setBarGap(0);
			histogram.setCategoryGap(0);
			histogram.setLegendVisible(false);
			histogram.setVerticalGridLinesVisible(false);
			for (int i = 0; i < Image.GRAY_LEVELS; ++i) {
				// Ignore other channels, except the first...
				series.getData().add(new Data<>(
					String.valueOf(i), rawHistogram[Image.GRAY][i] / factor));
			}
			return histogram;
		}
	}
