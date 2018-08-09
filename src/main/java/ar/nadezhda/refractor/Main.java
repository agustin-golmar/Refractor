
	package ar.nadezhda.refractor;

	import ar.nadezhda.refractor.config.RefractorProperties;
	import ar.nadezhda.refractor.support.FX;
	import javafx.application.Application;
	import javafx.scene.Scene;
	import javafx.stage.Stage;
	import org.springframework.beans.factory.UnsatisfiedDependencyException;
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.boot.context.properties.EnableConfigurationProperties;
	import org.springframework.context.ConfigurableApplicationContext;

	@SpringBootApplication(
		scanBasePackages = {
			"ar.nadezhda.refractor"
	})
	@EnableConfigurationProperties(RefractorProperties.class)
	public class Main extends Application {

		protected static ConfigurableApplicationContext context;

		public static void main(final String ... arguments) {
			try {
				FX.initialize();
				context = SpringApplication.run(Main.class, arguments);
				launch(arguments);
			}
			catch (final UnsatisfiedDependencyException exception) {
				System.exit(1);
			}
		}

		@Override
		public void start(final Stage stage)
				throws Exception {
			stage.setScene(context.getBean(Scene.class));
			stage.setTitle(context.getBean(RefractorProperties.class).getTitle());
			stage.show();
		}
	}
