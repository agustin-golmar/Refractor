package ar.nadezhda.refractor.handler.noise;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.handler.compression.LinearCompressor;
import ar.nadezhda.refractor.handler.unary.UnaryHandler;
import ar.nadezhda.refractor.interfaces.Compressor;

public class ExponentialNoiseHandler extends UnaryHandler {
    public ExponentialNoiseHandler() {
        super("exponentialNoise");
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(LinearCompressor.class);
	}
}
