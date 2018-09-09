package ar.nadezhda.refractor.handler.unary;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;

public class PowerHandler extends UnaryHandler {
    public PowerHandler() {
        super("power");
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
