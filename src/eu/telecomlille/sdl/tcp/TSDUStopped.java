package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUStopped extends Signal {
	public TSDUStopped(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "TSDUStopped()";
	}
}
