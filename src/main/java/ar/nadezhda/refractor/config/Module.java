
	package ar.nadezhda.refractor.config;

	import ar.nadezhda.refractor.format.BMPFormat;
	import ar.nadezhda.refractor.format.GIFFormat;
	import ar.nadezhda.refractor.format.JPGFormat;
	import ar.nadezhda.refractor.format.PGMFormat;
	import ar.nadezhda.refractor.format.PNGFormat;
	import ar.nadezhda.refractor.format.PPMFormat;
	import ar.nadezhda.refractor.format.RAWFormat;
	import ar.nadezhda.refractor.format.TIFFormat;
	import ar.nadezhda.refractor.format.WBMPFormat;
    import ar.nadezhda.refractor.handler.*;
    import ar.nadezhda.refractor.interfaces.Handler;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
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
			return FXMLLoader.load(layout.getURL());
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
			router.put("save", new SaveHandler());
			router.put("copy", new CopyHandler());
			router.put("remove", new RemoveHandler());
			router.put("display", new DisplayHandler());
			router.put("rgbSplit", new RGBSplitHandler());
			router.put("add", new AddHandler());
			router.put("sub", new SubHandler());
			router.put("product", new ProductHandler());
			router.put("scalarprod", new ScalarProdHandler());
			router.put("negative",new NegativeHandler());
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
	}
