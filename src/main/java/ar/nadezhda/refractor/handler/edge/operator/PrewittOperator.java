
	package ar.nadezhda.refractor.handler.edge.operator;

	import ar.nadezhda.refractor.interfaces.DerivativeOperator;

	public class PrewittOperator implements DerivativeOperator {

		@Override
		public double [][] dX() {
			return new double [][] {
				{-1, 0, 1},
				{-1, 0, 1},
				{-1, 0, 1}
			};
		}

		@Override
		public double [][] dY() {
			return new double [][] {
				{-1, -1, -1},
				{0, 0, 0},
				{1, 1, 1}
			};
		}

		@Override
		public double [][] dSW() {
			return new double [][] {
				{0, -1, -1},
				{1, 0, -1},
				{1, 1, 0}
			};
		}

		@Override
		public double [][] dSE() {
			return new double [][] {
				{-1, -1, 0},
				{-1, 0, 1},
				{0, 1, 1}
			};
		}
	}
