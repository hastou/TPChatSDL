package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUStop extends Signal {
	public TSDUStop(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "TSDUClose()";
	}
}
