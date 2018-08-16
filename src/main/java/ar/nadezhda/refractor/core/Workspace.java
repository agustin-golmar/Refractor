
	package ar.nadezhda.refractor.core;

	import ar.nadezhda.refractor.config.RefractorProperties;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import javax.inject.Inject;
	import org.springframework.stereotype.Service;

	@Service
	public class Workspace {

		protected final RefractorProperties config;
		protected final Map<String, ImageFormat> formats;

		@Inject
		public Workspace(
				final RefractorProperties config,
				final List<ImageFormat> availableFormats) {
			this.config = config;
			this.formats = new HashMap<>();
			availableFormats.forEach(format -> {
				System.out.println("Formats: " + format);
				this.formats.put(format.getExtension(), format);
			});
		}

		public Optional<Image> loadImageUsingConfig(final String path) {
			final Optional<Integer> index = config
					.findImage(getFilename(path).toLowerCase());
			if (index.isPresent()) {
				final int i = index.get();
				final int width = config.getDimensions()[i][0];
				final int height = config.getDimensions()[i][1];
				return Optional
					.ofNullable(formats.get(getExtension(path)))
					.map(format -> {
						try {
							return format.getBytes(path, width, height);
						}
						catch (final IOException exception) {
							exception.printStackTrace();
							return null;
						}
					})
					.filter(raw -> raw != null)
					.map(Image::new);
			}
			return Optional.empty();
		}

		public Optional<Image> loadImage(final String path) {
			return Optional
				.ofNullable(formats.get(getExtension(path)))
				.map(format -> {
					try {
						return format.getBytes(path);
					}
					catch (final IOException exception) {
						exception.printStackTrace();
						return null;
					}
				})
				.filter(raw -> raw != null)
				.map(Image::new);
		}

		public static String getExtension(final String filename) {
			final int index = filename.lastIndexOf(".");
			if (0 <= index && index < filename.length()) {
				return filename
						.toLowerCase()
						.substring(1 + index);
			}
			else return "";
		}

		public static String getFilename(final String path) {
			final int index = path.lastIndexOf("/");
			if (0 <= index && index < path.length()) {
				return path.substring(1 + index);
			}
			else return path;
		}
	}
