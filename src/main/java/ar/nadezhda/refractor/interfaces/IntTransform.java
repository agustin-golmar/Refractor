
	package ar.nadezhda.refractor.interfaces;

	@FunctionalInterface
	public interface IntTransform {

		int apply(final double[][][] space,
                     final int c, final int w, final int h);
	}
