package ar.nadezhda.refractor.handler.noise;

import ar.nadezhda.refractor.handler.unary.UnaryHandler;

public class ExponentialNoiseHandler extends UnaryHandler {
    public ExponentialNoiseHandler() {
        super("exponentialNoise", false,true);
    }
}
