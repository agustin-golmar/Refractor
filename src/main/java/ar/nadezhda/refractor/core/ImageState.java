
	package ar.nadezhda.refractor.core;

	import java.awt.Point;
	import java.util.function.DoubleBinaryOperator;
	import javafx.scene.image.ImageView;
	import javafx.scene.layout.Pane;
	import javafx.scene.paint.Color;
	import javafx.scene.shape.Rectangle;

	public class ImageState {

		protected final Rectangle area;
		protected final ImageView view;
		protected final Image image;
		protected final Pane root;
		protected final Point anchor;
		protected final String key;

		public ImageState(final String key, final ImageView view, final Image image) {
			this.key = key;
			this.anchor = new Point(0, 0);
			this.area = new Rectangle();
			this.area.setMouseTransparent(true);
			this.area.setFill(Color.rgb(255, 255, 255, 0.1));
			this.area.setStroke(Color.BLACK);
			this.area.getStrokeDashArray().add(5.0);
			this.area.setManaged(false);
			this.root = (Pane) view.getParent();
			this.root.getChildren().add(area);
			this.view = view;
			this.image = image;
		}

		public String getKey() {
			return key;
		}

		public ImageView getView() {
			return view;
		}

		public Image getImage() {
			return image;
		}

		public Pane getRoot() {
			return root;
		}

		public ImageState setStartArea(final double x, final double y) {
			anchor.setLocation(
					Math.max(0, Math.min(x, image.getWidth())),
					Math.max(0, Math.min(y, image.getHeight())));
			area.setX(anchor.x);
			area.setY(anchor.y);
			return resetArea();
		}

		public ImageState updateArea(final double x, final double y) {
			final double sx = Math.max(0, Math.min(x, image.getWidth()));
			final double sy = Math.max(0, Math.min(y, image.getHeight()));
			area.setX(Math.min(sx, anchor.x));
			area.setWidth(Math.abs(anchor.x - sx));
			area.setY(Math.min(sy, anchor.y));
			area.setHeight(Math.abs(anchor.y - sy));
			return this;
		}

		public ImageState resetArea() {
			area.setWidth(0);
			area.setHeight(0);
			return this;
		}

		public int getXArea() {
			return (int) area.getWidth();
		}

		public int getYArea() {
			return (int) area.getHeight();
		}

		public int getPixelCount() {
			return getXArea() * getYArea();
		}

		public Image getSelectedImage() {
			final byte [][][] raw = new byte [image.getChannels()][getXArea()][getYArea()];
			final int x = (int) area.getX();
			final int y = (int) area.getY();
			final int wEnd = (int) (x + area.getWidth());
			final int hEnd = (int) (y + area.getHeight());
			for (int c = 0; c < image.getChannels(); ++c)
				for (int h = y; h < hEnd; ++h)
					for (int w = x; w < wEnd; ++w) {
						raw[c][w - x][h - y] = image.rawData[c][w][h];
					}
			return new Image(image.getSource(), raw);
		}

		public double [] getRGBAverageOnArea() {
			final double [] avg = {0, 0, 0};
			final double area = getPixelCount();
			final int x = (int) this.area.getX();
			final int y = (int) this.area.getY();
			final int wEnd = (int) (x + this.area.getWidth());
			final int hEnd = (int) (y + this.area.getHeight());
			if (image.getChannels() == 3) {
				for (int h = y; h < hEnd; ++h) {
					for (int w = x; w < wEnd; ++w) {
						avg[0] += image.red()[w][h];
						avg[1] += image.green()[w][h];
						avg[2] += image.blue()[w][h];
					}
				}
			}
			else {
				for (int h = y; h < hEnd; ++h) {
					for (int w = x; w < wEnd; ++w) {
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

		public Image operate(final ImageState other, final DoubleBinaryOperator op) {
			if (this.image.getHeight() != other.image.getHeight()
					|| this.image.getWidth() != other.image.getWidth()
					|| this.image.getChannels() != other.image.getChannels()) {
				throw new IllegalArgumentException("Mismo tamaño por favor.");
			}
			double minData = this.image.data[0][0][0] + other.image.data[0][0][0];
			double maxData = minData;
			Image res = new Image(
					this.image.source + "_" + other.image.source,
					this.image.getChannels(),
					this.image.getWidth(), this.image.getHeight());
			for (int c = 0; c < this.image.getChannels(); c++) {
				for (int w = 0; w < this.image.getWidth(); w++) {
					for (int h = 0; h < this.image.getHeight(); h++) {
						res.data[c][w][h] = op.applyAsDouble(
								this.image.data[c][w][h],
								other.image.data[c][w][h]);
						if (res.data[c][w][h] > maxData)
							maxData = res.data[c][w][h];
						else if (res.data[c][w][h] < minData)
							minData = res.data[c][w][h];
					}
				}
			}
			for (int c = 0; c < this.image.getChannels(); c++) {
				for (int w = 0; w < this.image.getWidth(); w++) {
					for (int h = 0; h < this.image.getHeight(); h++) {
						res.data[c][w][h] = (res.data[c][w][h] - minData) / (maxData - minData) * 255;
						res.rawData[c][w][h] = (byte) res.data[c][w][h];
					}
				}
			}
			return res;
		}
	}
