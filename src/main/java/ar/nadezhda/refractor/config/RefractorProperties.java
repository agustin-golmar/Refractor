
	package ar.nadezhda.refractor.config;

	import java.util.Optional;
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

		protected String [] images;
		protected int [][] dimensions;

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

		public String [] getImages() {
			return images;
		}

		public int [][] getDimensions() {
			return dimensions;
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

		public void setImages(final String [] images) {
			this.images = images;
		}

		public void setDimensions(final int [][] dimensions) {
			this.dimensions = dimensions;
		}

		public Optional<Integer> findImage(final String filename) {
			for (int i = 0; i < images.length; ++i)
				if (images[i].equals(filename))
					return Optional.of(i);
			return Optional.empty();
		}
	}
