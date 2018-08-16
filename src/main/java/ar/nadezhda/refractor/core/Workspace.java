
	package ar.nadezhda.refractor.core;

	import ar.nadezhda.refractor.config.RefractorProperties;
	import ar.nadezhda.refractor.interfaces.ImageFormat;
	import ar.nadezhda.refractor.support.Tool;
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
				this.formats.put(format.getExtension(), format);
			});
		}

		public Optional<Image> loadImageUsingConfig(final String path) {
			final Optional<Integer> index = config
					.findImage(Tool.getFilename(path).toLowerCase());
			if (index.isPresent()) {
				final int i = index.get();
				final int width = config.getDimensions()[i][0];
				final int height = config.getDimensions()[i][1];
				return Optional
					.ofNullable(formats.get(Tool.getExtension(path)))
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
				.ofNullable(formats.get(Tool.getExtension(path)))
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

		public Workspace saveImage(final Image image, final String path) {
			Optional
				.ofNullable(formats.getOrDefault(
						Tool.getExtension(path), formats.get("bmp")))
				.ifPresent(format -> {
					try {
						format.save(image, path);
					}
					catch (final IOException exception) {
						exception.printStackTrace();
					}
				});
			return this;
		}
	}
