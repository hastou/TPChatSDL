package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUClosed extends Signal {
	public TSDUClosed(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "TSDUClosed()";
	}
}
