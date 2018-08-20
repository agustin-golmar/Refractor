
	package ar.nadezhda.refractor.core;

	import java.awt.Point;
	import javafx.scene.image.ImageView;

	public class ImageState {

		protected final ImageView view;
		protected final Image image;
		protected Point startArea;
		protected Point endArea;

		public ImageState(final ImageView view, final Image image) {
			this.view = view;
			this.image = image;
			this.startArea = new Point(0, 0);
			this.endArea = new Point(0, 0);
		}

		public ImageView getView() {
			return view;
		}

		public Image getImage() {
			return image;
		}

		public ImageState setStartArea(final double x, final double y) {
			startArea.setLocation(x, y);
			return this;
		}

		public ImageState setEndArea(final double x, final double y) {
			endArea.setLocation(x, y);
			return this;
		}

		public ImageState resetArea() {
			startArea.setLocation(0, 0);
			endArea.setLocation(0, 0);
			return this;
		}

		public int getXArea() {
			return Math.abs(endArea.x - startArea.x);
		}

		public int getYArea() {
			return Math.abs(endArea.y - startArea.y);
		}

		public int pixelCount() {
			return getXArea() * getYArea();
		}

		public double [] getRGBAverageOnArea() {
			final double [] avg = {0, 0, 0};
			final double area = pixelCount();
			final int wStart = Math.min(startArea.x, endArea.x);
			final int wEnd = Math.max(startArea.x, endArea.x);
			final int hStart = Math.min(startArea.y, endArea.y);
			final int hEnd = Math.max(startArea.y, endArea.y);
			if (image.getChannels() == 3) {
				for (int h = hStart; h < hEnd; ++h) {
					for (int w = wStart; w < wEnd; ++w) {
						avg[0] += image.red()[w][h];
						avg[1] += image.green()[w][h];
						avg[2] += image.blue()[w][h];
					}
				}
			}
			else {
				for (int h = hStart; h < hEnd; ++h) {
					for (int w = wStart; w < wEnd; ++w) {
						avg[0] += image.gray()[w][h];
					}
				}
				avg[1] = avg[0];
				avg[2] = avg[0];
			}
			if (0 < area) {
				avg[0] /= area;
				avg[1] /= area;
				avg[2] /= area;
			}
			else {
				avg[0] = avg[1] = avg[2] = 0;
			}
			return avg;
		}
	}
