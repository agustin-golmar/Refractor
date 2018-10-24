package ar.nadezhda.refractor.interfaces;

@FunctionalInterface
public interface FlatIntTransform {

	int apply(final int [][] space, final int w, final int h);
}
