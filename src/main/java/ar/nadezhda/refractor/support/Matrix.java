
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
	}
