
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
		protected final Map<String, ImageState> images;

		@Inject
		public Workspace(
				final RefractorProperties config,
				final List<ImageFormat> availableFormats) {
			this.config = config;
			this.formats = new HashMap<>();
			this.images = new HashMap<>();
			availableFormats.forEach(format -> {
				this.formats.put(format.getExtension(), format);
			});
		}

		public Workspace addState(final String key, final ImageState state) {
			images.put(key, state);
			return this;
		}

		public Optional<ImageState> getState(final String key) {
			return Optional.ofNullable(images.get(key));
		}

		public Workspace removeState(final String key) {
			images.remove(key);
			return this;
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
					.map(data -> new Image(path, data));
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
				.map(data -> new Image(path, data));
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
