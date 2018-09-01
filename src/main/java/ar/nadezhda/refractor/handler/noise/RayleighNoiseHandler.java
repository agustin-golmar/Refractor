package ar.nadezhda.refractor.handler.noise;

import ar.nadezhda.refractor.handler.unary.UnaryHandler;

public class RayleighNoiseHandler extends UnaryHandler {
    public RayleighNoiseHandler() {
        super("rayleighNoise", false,true);
    }
}
