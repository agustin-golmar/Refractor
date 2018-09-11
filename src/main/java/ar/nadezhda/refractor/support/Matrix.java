
	package ar.nadezhda.refractor.support;

	import ar.nadezhda.refractor.interfaces.Transform;

	public class Matrix {

		public static double [][][] emptySpaceFrom(final double [][][] space) {
			return new double [space.length][space[0].length][space[0][0].length];
		}

		public static double [][][] filter(
				final double [][][] space, final Transform transform) {
			final double [][][] result = emptySpaceFrom(space);
			for (int h = 0; h < space[0][0].length; ++h)
				for (int w = 0; w < space[0].length; ++w)
					for (int c = 0; c < space.length; ++c) {
						result[c][w][h] = transform.apply(space, c, w, h);
					}
			return result;
		}

		public static double [][][] convolution(
				final double [][][] space, final double [][] kernel) {
			final var dim = kernel.length;
			final var base = dim/2;
			final var width = space[0].length;
			final var height = space[0][0].length;
			return Matrix.filter(space, (space_, c, w, h) -> {
				double result = 0.0;
				for (int i = 0; i < dim; ++i)
					for (int j = 0; j < dim; ++j) {
						final var ew = w - base + i;
						final var eh = h - base + j;
						if (-1 < ew && ew < width && -1 < eh && eh < height)
							result += kernel[i][j] * space[c][ew][eh];
					}
				return result;
			});
		}

		public static double [][][] absoluteGradient(
				final double [][][] dx, final double [][][] dy) {
			return Matrix.filter(dx, (dx_, c, w, h) -> Math.hypot(dx[c][w][h], dy[c][w][h]));
		}

		public static double [][][] hRoots(
				final double [][][] space, final double slope) {
			final var width = space[0].length;
			return Matrix.filter(space, (space_, c, w, h) -> {
				final var k = space[c][w][h];
				final var k1 = w + 1 < width? space[c][w + 1][h] : 0.0;
				if (signChange(k, k1)) {
					return slope < Math.abs(k) + Math.abs(k1)? 255.0 : 0.0;
				}
				else if (k1 == 0.0) {
					final var k2 = w + 2 < width? space[c][w + 2][h] : 0.0;
					if (signChange(k, k2)) {
						return slope < Math.abs(k) + Math.abs(k2)? 255.0 : 0.0;
					}
					else return 0.0;
				}
				else return 0.0;
			});
		}

		public static double [][][] vRoots(
				final double [][][] space, final double slope) {
			final var height = space[0][0].length;
			return Matrix.filter(space, (space_, c, w, h) -> {
				final var k = space[c][w][h];
				final var k1 = h + 1 < height? space[c][w][h + 1] : 0.0;
				if (signChange(k, k1)) {
					return slope < Math.abs(k) + Math.abs(k1)? 255.0 : 0.0;
				}
				else if (k1 == 0.0) {
					final var k2 = h + 2 < height? space[c][w][h + 2] : 0.0;
					if (signChange(k, k2)) {
						return slope < Math.abs(k) + Math.abs(k2)? 255.0 : 0.0;
					}
					else return 0.0;
				}
				else return 0.0;
			});
		}

		public static double [][][] roots(
				final double [][][] space, final double slope) {
			final var hRoots = Matrix.hRoots(space, slope);
			final var vRoots = Matrix.vRoots(space, slope);
			return Matrix.filter(space, (space_, c, w, h) -> {
				if (0.0 < hRoots[c][w][h]) return 255.0;
				if (0.0 < vRoots[c][w][h]) return 255.0;
				return 0.0;
			});
		}

		public static boolean signChange(final double x, final double y) {
			return (Math.signum(x) == +1.0 && Math.signum(y) == -1.0)
				|| (Math.signum(x) == -1.0 && Math.signum(y) == +1.0);
		}

		public static double [][] marrHildreth(final int dim, final double σ) {
			final var filter = new double [dim][dim];
			final var S2PI = Math.sqrt(2.0 * Math.PI);
			final var σ2 = σ*σ;
			for (int i = 0; i < dim; ++i)
				for (int j = 0; j < dim; ++j) {
					final var x = i - dim/2;
					final var y = j - dim/2;
					final var factor = (x*x + y*y) / σ2;
					filter[i][j] = (factor - 2) * Math.exp(-factor/2.0) / (S2PI*σ*σ2);
				}
			return filter;
		}
	}
