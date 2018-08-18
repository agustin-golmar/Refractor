
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
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import javafx.fxml.FXMLLoader;
	import javafx.scene.Parent;
	import javafx.scene.Scene;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.List;
	import javax.inject.Inject;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.ComponentScan;
	import org.springframework.context.annotation.Configuration;
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

		@Bean
		public Parent parent()
				throws IOException {
			return FXMLLoader.load(layout.getURL());
		}

		@Bean
		public Scene scene(final Parent root) {
			return new Scene(root, properties.getWidth(), properties.getHeight());
		}

		@Bean
		public List<ImageFormat> availableFormats() {
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
	}
