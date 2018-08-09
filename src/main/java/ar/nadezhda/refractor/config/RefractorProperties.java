
	package ar.nadezhda.refractor.config;

	import javax.validation.constraints.NotBlank;
	import javax.validation.constraints.Positive;
	import org.springframework.boot.context.properties.ConfigurationProperties;
	import org.springframework.validation.annotation.Validated;

	@Validated
	@ConfigurationProperties("refractor")
	public class RefractorProperties {

		@NotBlank protected String title;
		@NotBlank protected String layout;
		@Positive protected int width;
		@Positive protected int height;

		public String getTitle() {
			return title;
		}

		public String getLayout() {
			return layout;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public void setTitle(final String title) {
			this.title = title;
		}

		public void setLayout(final String layout) {
			this.layout = layout;
		}

		public void setWidth(final int width) {
			this.width = width;
		}

		public void setHeight(final int height) {
			this.height = height;
		}
	}
