
	package ar.nadezhda.refractor.core;

	import java.awt.Point;
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

		public ImageState(final ImageView view, final Image image) {
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

		public int pixelCount() {
			return getXArea() * getYArea();
		}

		public double [] getRGBAverageOnArea() {
			final double [] avg = {0, 0, 0};
			final double area = pixelCount();
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
	}
