package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUListening extends Signal {
	public TSDUListening(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "TSDUListening()";
	}
}
