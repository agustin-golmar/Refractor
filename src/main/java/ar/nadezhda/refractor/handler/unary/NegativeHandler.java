package ar.nadezhda.refractor.handler.unary;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;

public class NegativeHandler extends UnaryHandler {

    public NegativeHandler() {
        super("negative");
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
