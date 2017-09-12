package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUOpen extends Signal {
	public IProcess peer;
	public TSDUOpen(IProcess sender, IProcess peer) {
		super(sender);
		this.peer = peer;
	}

	@Override
	public String toString() {
		return "TSDUOpen()";
	}
}
