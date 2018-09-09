package ar.nadezhda.refractor.handler.unary;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.handler.compression.DynamicRangeCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;

public class ScalarProdHandler extends UnaryHandler {

    public ScalarProdHandler() {
        super("scalarProd");
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(DynamicRangeCompressor.class);
	}
}
