package ar.nadezhda.refractor.handler.noise;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.handler.unary.UnaryHandler;
import ar.nadezhda.refractor.interfaces.Compressor;

public class SaltAndPepperHandler extends UnaryHandler {
    public SaltAndPepperHandler() {
        super("saltAndPepper");
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
