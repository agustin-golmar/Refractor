
	package ar.nadezhda.refractor.config;

	import ar.nadezhda.refractor.Main;
	import ar.nadezhda.refractor.format.*;
	import ar.nadezhda.refractor.handler.*;
	import ar.nadezhda.refractor.handler.binary.*;
	import ar.nadezhda.refractor.handler.edge.*;
	import ar.nadezhda.refractor.handler.edge.operator.ConvolutionHandler;
	import ar.nadezhda.refractor.handler.feature.*;
	import ar.nadezhda.refractor.handler.filter.*;
    import ar.nadezhda.refractor.handler.filter.advanced.*;
    import ar.nadezhda.refractor.handler.noise.*;
	import ar.nadezhda.refractor.handler.resource.*;
    import ar.nadezhda.refractor.handler.threshold.GlobalThresholdHandler;
    import ar.nadezhda.refractor.handler.threshold.OtzuThresholdHandler;
    import ar.nadezhda.refractor.handler.unary.*;
	import ar.nadezhda.refractor.interfaces.Handler;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import javafx.collections.ObservableMap;
	import javafx.fxml.FXMLLoader;
	import javafx.scene.Parent;
	import javafx.scene.Scene;
	import javafx.stage.FileChooser.ExtensionFilter;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javax.inject.Inject;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.ComponentScan;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.context.annotation.Lazy;
	import org.springframework.core.io.Resource;

	@Configuration
	@ComponentScan("ar.nadezhda.refractor")
	public class Module {

		protected final RefractorProperties properties;
		protected final Resource layout;
		protected ObservableMap<String, Object> namespace;

		@Inject
		public Module(
				final RefractorProperties properties,
				@Value("${refractor.layout}") final Resource layout) {
			this.properties = properties;
			this.layout = layout;
		}

		@Bean @Lazy
		public Parent parent()
				throws IOException {
			final FXMLLoader loader = new FXMLLoader(layout.getURL());
			final Parent parent = loader.load();
			namespace = loader.getNamespace();
			Main.namespace = namespace;
			return parent;
		}

		@Bean @Lazy
		public Scene scene(final Parent root) {
			return new Scene(root, properties.getWidth(), properties.getHeight());
		}

		@Bean("router") @Lazy
		public Map<String, Handler> router() {
			// Se pueden cargar por reflection?
			final Map<String, Handler> router = new HashMap<>();
			router.put("open", new OpenHandler());
			router.put("openRaw", new OpenRAWHandler());
			router.put("save", new SaveHandler());
			router.put("copy", new CopyHandler());
			router.put("remove", new RemoveHandler());
			router.put("display", new DisplayHandler());
			router.put("grayscale", new GrayscaleHandler());
			router.put("add", new AddHandler());
			router.put("sub", new SubHandler());
			router.put("product", new ProductHandler());
			router.put("histogram", new HistogramHandler());
			router.put("equalizer", new EqualizerHandler());
			router.put("scalarProd", new ScalarProdHandler());
			router.put("negative", new NegativeHandler());
			router.put("threshold", new ThresholdHandler());
			router.put("power", new PowerHandler());
			router.put("increaseContrast", new IncreaseContrastHandler());
			router.put("gaussianNoise", new GaussianNoiseHandler());
			router.put("exponentialNoise", new ExponentialNoiseHandler());
			router.put("saltAndPepper", new SaltAndPepperHandler());
			router.put("rayleighNoise", new RayleighNoiseHandler());
			router.put("meanFilter", new MeanFilterHandler());
			router.put("gaussFilter", new GaussFilterHandler());
			router.put("medianFilter", new MedianFilterHandler());
			router.put("weightMedianFilter", new WeightedMedianFilterHandler());
			router.put("highpassFilter", new HighpassFilterHandler());
			router.put("gradient", new GradientEdgeDetectorHandler());
			router.put("convolution", new ConvolutionHandler());
			router.put("laplacian", new LaplacianHandler());
			router.put("marrHildreth", new MarrHildrethHandler());
			router.put("bilateral",new BilateralFilterHandler());
			router.put("anisotropic",new AnisotropicFilterHandler());
            router.put("isotropic",new IsotropicFilterHandler());
            router.put("globalThreshold",new GlobalThresholdHandler());
            router.put("otsuThreshold",new OtzuThresholdHandler());
            router.put("canny",new CannyHandler());
            router.put("susan", new SUSANHandler());
            router.put("levelSet", new LevelSetHandler());
            router.put("houghLine",new HoughLineHandler());
            router.put("video", new VideoHandler());
			return router;
		}

		@Bean
		public List<ImageFormat> availableFormats() {
			// Se pueden cargar por reflection?
			final List<ImageFormat> formats = new ArrayList<>();
			formats.add(new BMPFormat());
			formats.add(new GIFFormat());
			formats.add(new JPGFormat());
			formats.add(new PGMFormat());
			formats.add(new PNGFormat());
			formats.add(new PPMFormat());
			formats.add(new RAWFormat());
			formats.add(new TIFFormat());
			formats.add(new WBMPFormat());
			formats.add(new JPEGFormat());
			return formats;
		}

		@Bean("filters")
		public ExtensionFilter [] filters(final List<ImageFormat> formats) {
			final ExtensionFilter [] filters = new ExtensionFilter [1 + formats.size()];
			filters[0] = new ExtensionFilter("All files", "*.*");
			for (int i = 1; i < filters.length; ++i) {
				final ImageFormat format = formats.get(i - 1);
				filters[i] = new ExtensionFilter(
					format.getExtension().toUpperCase(),
					"*." + format.getExtension().toLowerCase());
			}
			return filters;
		}

		@Bean("namespace") @Lazy
		public Map<String, Object> namespace() {
			return namespace;
		}
	}
