package ar.nadezhda.refractor.support;

import ar.nadezhda.refractor.interfaces.FlatIntTransform;
import ar.nadezhda.refractor.interfaces.IntTransform;
import ar.nadezhda.refractor.interfaces.KernelOperator;
import ar.nadezhda.refractor.interfaces.Transform;
import java.awt.Point;

public class Matrix {

	public static final double NOTHING = 0.0;
	public static final double BORDER = 0.5;
	public static final double CORNER = 1.0;

	public static final double[][] CONTOUR_COLORS = {
		{0.0, 255.0, 0.0},		// Verde
		{255.0, 0.0, 0.0},		// Rojo
		{0.0, 0.0, 255.0},		// Azul
		{255.0, 255.0, 0.0},	// Amarillo
		{255.0, 0.0, 128.0},	// Violeta
		{255.0, 128.0, 0.0},	// Naranja
		{128.0, 255.0, 255.0}	// Celeste
	};

	public static double[][][] emptySpaceFrom(final double[][][] space) {
		return new double[space.length][space[0].length][space[0][0].length];
	}

	public static int[][] emptySpaceFrom(final int[][] space) {
		return new int[space.length][space[0].length];
	}

	public static int[][] flatIntAndEmptySpaceFrom(final double[][][] space) {
		return new int[space[0].length][space[0][0].length];
	}

	public static int[][] flatFilterToInt(final double[][][] space, final IntTransform transform) {
		final int[][] result = flatIntAndEmptySpaceFrom(space);
		for (int h = 0; h < space[0][0].length; ++h)
			for (int w = 0; w < space[0].length; ++w) {
				result[w][h] = transform.apply(space, 0, w, h);
			}
		return result;
	}

	public static double[][][] filter(final double[][][] space, final Transform transform) {
		final double[][][] result = emptySpaceFrom(space);
		for (int h = 0; h < space[0][0].length; ++h)
			for (int w = 0; w < space[0].length; ++w)
				for (int c = 0; c < space.length; ++c) {
					result[c][w][h] = transform.apply(space, c, w, h);
				}
		return result;
	}

	public static int[][] filter(final int[][] space, final FlatIntTransform transform) {
		final int[][] result = emptySpaceFrom(space);
		for (int h = 0; h < space[0].length; ++h)
			for (int w = 0; w < space.length; ++w) {
				result[w][h] = (int) transform.apply(space, w, h);
			}
		return result;
	}

	public static double[][][] convolution(final double[][][] space, final double[][] kernel) {
		return convolution(space, kernel, (c, w, h, k, s) -> k * s);
	}

	public static int[][] convolution(final int[][] space, final double[][] kernel) {
		return convolution(space, kernel, (c, w, h, k, s) -> k * s);
	}

	public static double convolution(final int[][] space, final double[][] kernel, final Point point) {
		final var dim = kernel.length;
		final var base = dim / 2;
		final var width = space.length;
		final var height = space[0].length;
		double result = 0.0;
		for (int i = 0; i < dim; ++i)
			for (int j = 0; j < dim; ++j) {
				final var ew = point.x - base + i;
				final var eh = point.y - base + j;
				if (-1 < ew && ew < width && -1 < eh && eh < height)
					result += kernel[i][j] * space[ew][eh];
			}
		return result;
	}

	public static int[][] convolution(final int[][] space, final double[][] kernel, final KernelOperator operator) {
		final var dim = kernel.length;
		final var base = dim / 2;
		final var width = space.length;
		final var height = space[0].length;
		return Matrix.filter(space, (space_, w, h) -> {
			double result = 0.0;
			for (int i = 0; i < dim; ++i)
				for (int j = 0; j < dim; ++j) {
					final var ew = w - base + i;
					final var eh = h - base + j;
					if (-1 < ew && ew < width && -1 < eh && eh < height)
						result += operator.apply(0, w, h, kernel[i][j], space[ew][eh]);
				}
			return (int) result;
		});
	}

	public static double[][][] convolution(final double[][][] space, final double[][] kernel,
			final KernelOperator operator) {
		final var dim = kernel.length;
		final var base = dim / 2;
		final var width = space[0].length;
		final var height = space[0][0].length;
		return Matrix.filter(space, (space_, c, w, h) -> {
			double result = 0.0;
			for (int i = 0; i < dim; ++i)
				for (int j = 0; j < dim; ++j) {
					final var ew = w - base + i;
					final var eh = h - base + j;
					if (-1 < ew && ew < width && -1 < eh && eh < height)
						result += operator.apply(c, w, h, kernel[i][j], space[c][ew][eh]);
				}
			return result;
		});
	}

	public static double[][][] absoluteGradient(final double[][][] dx, final double[][][] dy) {
		return Matrix.filter(dx, (dx_, c, w, h) -> Math.hypot(dx[c][w][h], dy[c][w][h]));
	}

	public static double[][][] hRoots(final double[][][] space, final double slope) {
		final var width = space[0].length;
		return Matrix.filter(space, (space_, c, w, h) -> {
			final var k = space[c][w][h];
			final var k1 = w + 1 < width ? space[c][w + 1][h] : 0.0;
			if (signChange(k, k1)) {
				return slope < Math.abs(k) + Math.abs(k1) ? 255.0 : 0.0;
			} else if (k1 == 0.0) {
				final var k2 = w + 2 < width ? space[c][w + 2][h] : 0.0;
				if (signChange(k, k2)) {
					return slope < Math.abs(k) + Math.abs(k2) ? 255.0 : 0.0;
				} else
					return 0.0;
			} else
				return 0.0;
		});
	}

	public static double[][][] vRoots(final double[][][] space, final double slope) {
		final var height = space[0][0].length;
		return Matrix.filter(space, (space_, c, w, h) -> {
			final var k = space[c][w][h];
			final var k1 = h + 1 < height ? space[c][w][h + 1] : 0.0;
			if (signChange(k, k1)) {
				return slope < Math.abs(k) + Math.abs(k1) ? 255.0 : 0.0;
			} else if (k1 == 0.0) {
				final var k2 = h + 2 < height ? space[c][w][h + 2] : 0.0;
				if (signChange(k, k2)) {
					return slope < Math.abs(k) + Math.abs(k2) ? 255.0 : 0.0;
				} else
					return 0.0;
			} else
				return 0.0;
		});
	}

	public static double[][][] roots(final double[][][] space, final double slope) {
		final var hRoots = Matrix.hRoots(space, slope);
		final var vRoots = Matrix.vRoots(space, slope);
		return Matrix.filter(space, (space_, c, w, h) -> {
			if (0.0 < hRoots[c][w][h])
				return 255.0;
			if (0.0 < vRoots[c][w][h])
				return 255.0;
			return 0.0;
		});
	}

	public static boolean signChange(final double x, final double y) {
		return (Math.signum(x) == +1.0 && Math.signum(y) == -1.0) || (Math.signum(x) == -1.0 && Math.signum(y) == +1.0);
	}

	public static double[][] marrHildreth(final int dim, final double σ) {
		final var filter = new double[dim][dim];
		final var S2PI = Math.sqrt(2.0 * Math.PI);
		final var σ2 = σ * σ;
		for (int i = 0; i < dim; ++i)
			for (int j = 0; j < dim; ++j) {
				final var x = i - dim / 2;
				final var y = j - dim / 2;
				final var factor = (x * x + y * y) / σ2;
				filter[i][j] = (factor - 2) * Math.exp(-factor / 2.0) / (S2PI * σ * σ2);
			}
		return filter;
	}

	public static double[][] gaussian(final int dim, final double σ) {
		final var filter = new double[dim][dim];
		final var d1 = 2 * Math.PI * σ * σ;
		final var d2 = 2 * σ * σ;
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				final var x = i - dim / 2;
				final var y = j - dim / 2;
				filter[i][j] = Math.exp(-(x * x + y * y) / d2) / d1;
			}
		}
		return filter;
	}

	public static double[][][] colorFeatures(final double[][][] features, final double[][][] space) {
		final double[][][] result = emptySpaceFrom(space);
		for (int h = 0; h < space[0][0].length; ++h) {
			for (int w = 0; w < space[0].length; ++w) {
				if (features[0][w][h] == NOTHING) {
					result[0][w][h] = space[0][w][h];
					result[1][w][h] = space[1][w][h];
					result[2][w][h] = space[2][w][h];
				} else if (features[0][w][h] == BORDER) {
					result[0][w][h] = 255.0;
					result[1][w][h] = 230.0;
					result[2][w][h] = 40.0;
				} else if (features[0][w][h] == CORNER) {
					result[0][w][h] = 255.0;
					result[1][w][h] = 0.0;
					result[2][w][h] = 230.0;
				}
			}
		}
		return result;
	}

	public static double[][][] colorLevelSets(final int[][] levelSets, final double[][][] space) {
		final double[][][] result = emptySpaceFrom(space);
		for (int h = 0; h < space[0][0].length; ++h) {
			for (int w = 0; w < space[0].length; ++w) {
				final int color = levelSets[w][h];
				if (color == 0) {
					result[0][w][h] = space[0][w][h];
					result[1][w][h] = space[1][w][h];
					result[2][w][h] = space[2][w][h];
				}
				else {
					result[0][w][h] = CONTOUR_COLORS[color - 1][0];
					result[1][w][h] = CONTOUR_COLORS[color - 1][1];
					result[2][w][h] = CONTOUR_COLORS[color - 1][2];
				}
			}
		}
		return result;
	}

	public static double[][][] nonMaxSupression(final double[][][] borders, final double[][][] dx,
			final double[][][] dy) {
		final var res = new double[borders.length][borders[0].length][borders[0][0].length];
		for (int c = 0; c < res.length; c++) {
			for (int h = 1; h < res[0][0].length - 1; h++) {
				for (int w = 1; w < res[0].length - 1; w++) {
					if (borders[c][w][h] > 0.0001) {
						var angle = Math.atan2(dy[c][w][h], dx[c][w][h]);
						if (angle < 0)
							angle += Math.PI;
						int x, y;
						if (angle < Math.PI / 8 || angle > 7 * Math.PI / 8) {
							x = 0;
							y = 1;
						} else if (angle < 3 * Math.PI / 8) {
							x = 1;
							y = 1;
						} else if (angle < 5 * Math.PI / 8) {
							x = 1;
							y = 0;
						} else {
							x = -1;
							y = 1;
						}
						if (borders[c][w][h] > borders[c][w + x][h + y]
								&& borders[c][w][h] >= borders[c][w - x][h - y]) {
							res[c][w][h] = borders[c][w][h];
						}
					}
				}
			}
		}
		return res;
	}

	public static void hystheresis(final double[][][] borders, double t1, double t2) {
		for (int c = 0; c < borders.length; c++) {
			for (int w = 1; w < borders[0].length - 1; w++) {
				for (int h = 1; h < borders[0][0].length - 1; h++) {
					if (borders[c][w][h] < t1)
						borders[c][w][h] = 0;
					else if (borders[c][w][h] < t2 && borders[c][w + 1][h] == 0 && borders[c][w][h + 1] == 0
							&& borders[c][w + 1][h + 1] == 0 && borders[c][w - 1][h] == 0 && borders[c][w][h - 1] == 0
							&& borders[c][w - 1][h - 1] == 0)
						borders[c][w][h] = 0;
					else
						borders[c][w][h] = 255;

				}
			}
		}
	}

	public static double[][][] intersect(double[][][] m1, double[][][] m2) {
		var ret = new double[m1.length][m1[0].length][m1[0][0].length];
		for (int c = 0; c < ret.length; c++) {
			for (int w = 0; w < ret[0].length; w++) {
				for (int h = 0; h < ret[0][0].length; h++) {
					ret[c][w][h] = Math.max(m1[c][w][h], m2[c][w][h]);
				}
			}
		}
		return ret;
	}

	public static double[][][] prod(double[][][] m1, double[][][] m2) {
		var res = new double[m1.length][m1[0].length][m1[0][0].length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				for (int k = 0; k < m1[0][0].length; k++) {
					res[i][j][k] = m1[i][j][k] * m2[i][j][k];
				}
			}
		}
		return res;
	}

	public static double[][][] pow2(double[][][] m) {
		return prod(m, m);
	}
}
