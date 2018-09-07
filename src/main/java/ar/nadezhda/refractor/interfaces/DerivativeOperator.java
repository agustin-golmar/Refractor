
	package ar.nadezhda.refractor.interfaces;

	import ar.nadezhda.refractor.support.Matrix;

	public interface DerivativeOperator {

		public double [][] dX();
		public double [][] dY();
		public double [][] dSW();
		public double [][] dSE();

		public default double [][][] convolutionOverX(final double [][][] space) {
			return Matrix.convolution(space, dX());
		}

		public default double [][][] convolutionOverY(final double [][][] space) {
			return Matrix.convolution(space, dY());
		}

		public default double [][][] convolutionOverSW(final double [][][] space) {
			return Matrix.convolution(space, dSW());
		}

		public default double [][][] convolutionOverSE(final double [][][] space) {
			return Matrix.convolution(space, dSE());
		}
	}
