package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUConnected extends Signal {
	public TSDUConnected(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "TSDUConnected()";
	}
}
