
	package ar.nadezhda.refractor.support;

	public class Tool {

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
			final int index = Math
					.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
			if (0 <= index && index < path.length()) {
				return path.substring(1 + index);
			}
			else return path;
		}
	}
