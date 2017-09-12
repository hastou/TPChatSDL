package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * CloseUI request from ChatServerGUI to TCPServer.
 */
public class CloseUI extends Signal {
	public CloseUI(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "CloseUI()";
	}
}
