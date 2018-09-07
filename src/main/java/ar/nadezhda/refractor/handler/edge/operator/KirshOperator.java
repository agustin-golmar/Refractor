
	package ar.nadezhda.refractor.handler.edge.operator;

	import ar.nadezhda.refractor.interfaces.DerivativeOperator;

	public class KirshOperator implements DerivativeOperator {

		@Override
		public double [][] dX() {
			return new double [][] {
				{-3, -3, 5},
				{-3, 0, 5},
				{-3, -3, 5}
			};
		}

		@Override
		public double [][] dY() {
			return new double [][] {
				{-3, -3, -3},
				{-3, 0, -3},
				{5, 5, 5}
			};
		}

		@Override
		public double [][] dSW() {
			return new double [][] {
				{-3, -3, -3},
				{5, 0, -3},
				{5, 5, -3}
			};
		}

		@Override
		public double [][] dSE() {
			return new double [][] {
				{-3, -3, -3},
				{-3, 0, 5},
				{-3, 5, 5}
			};
		}
	}
