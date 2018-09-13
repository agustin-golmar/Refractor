
	package ar.nadezhda.refractor.interfaces;

	import ar.nadezhda.refractor.support.Matrix;

	public interface DerivativeOperator {

		double [][] dX();
		double [][] dY();
		double [][] dSW();
		double [][] dSE();

		default double [][][] convolutionOverX(final double[][][] space) {
			return Matrix.convolution(space, dX());
		}

		default double [][][] convolutionOverY(final double[][][] space) {
			return Matrix.convolution(space, dY());
		}

		default double [][][] convolutionOverSW(final double[][][] space) {
			return Matrix.convolution(space, dSW());
		}

		default double [][][] convolutionOverSE(final double[][][] space) {
			return Matrix.convolution(space, dSE());
		}
	}
