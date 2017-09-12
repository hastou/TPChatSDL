package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * ClosedUI request from TCPServer to ChatServerGUI.
 */
public class ClosedUI extends Signal {
	public ClosedUI(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "ClosedUI()";
	}
}
