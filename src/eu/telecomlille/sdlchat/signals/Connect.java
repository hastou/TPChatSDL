package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Enter the chat system.
 * 
 * @author Christophe TOMBELLE
 */
public class Connect extends Signal {
	public Connect(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "Connect()";
	}
}
