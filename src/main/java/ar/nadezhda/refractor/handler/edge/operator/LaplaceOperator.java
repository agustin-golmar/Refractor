
	package ar.nadezhda.refractor.handler.edge.operator;

	import ar.nadezhda.refractor.interfaces.DerivativeOperator;

	public class LaplaceOperator implements DerivativeOperator {

		@Override
		public double [][] dX() {
			return new double [][] {
				{0, 1, 0},
				{1, -4, 1},
				{0, 1, 0}
			};
		}

		@Override
		public double [][] dY() {
			return dX();
		}

		@Override
		public double [][] dSW() {
			return dX();
		}

		@Override
		public double [][] dSE() {
			return dX();
		}
	}
