package ar.nadezhda.refractor.interfaces;

@FunctionalInterface
public interface KernelOperator {

	public double apply(final int c, final int w, final int h,
			final double kernelValue, final double spaceValue);
}
