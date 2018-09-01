package ar.nadezhda.refractor.handler.noise;

import ar.nadezhda.refractor.handler.unary.UnaryHandler;

public class GaussianNoiseHandler extends UnaryHandler {
    public GaussianNoiseHandler() {
        super("gaussianNoise", true,false);
    }
}
