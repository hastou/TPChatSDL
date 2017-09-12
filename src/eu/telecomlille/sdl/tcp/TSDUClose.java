package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUClose extends Signal {
	public TSDUClose(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "TSDUClose()";
	}
}
