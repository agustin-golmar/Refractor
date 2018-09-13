
	package ar.nadezhda.refractor.interfaces;

	@FunctionalInterface
	public interface Transform {

		double apply(final double[][][] space,
                     final int c, final int w, final int h);
	}
