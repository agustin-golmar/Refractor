
	package ar.nadezhda.refractor.handler.edge.operator;

	import ar.nadezhda.refractor.interfaces.DerivativeOperator;

	public class CustomOperator implements DerivativeOperator {

		@Override
		public double [][] dX() {
			return new double [][] {
				{-1, 1, 1},
				{-1, -2, 1},
				{-1, 1, 1}
			};
		}

		@Override
		public double [][] dY() {
			return new double [][] {
				{-1, -1, -1},
				{1, -2, 1},
				{1, 1, 1}
			};
		}

		@Override
		public double [][] dSW() {
			return new double [][] {
				{1, -1, -1},
				{1, -2, -1},
				{1, 1, 1}
			};
		}

		@Override
		public double [][] dSE() {
			return new double [][] {
				{-1, -1, 1},
				{-1, -2, 1},
				{1, 1, 1}
			};
		}
	}
