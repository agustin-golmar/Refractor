
	package ar.nadezhda.refractor.config;

	import javafx.fxml.FXMLLoader;
	import javafx.scene.Parent;
	import javafx.scene.Scene;
	import java.io.IOException;
	import javax.inject.Inject;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.core.io.Resource;

	@Configuration
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
	}
