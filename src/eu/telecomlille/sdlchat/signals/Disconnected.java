package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Disconnection indication from the ChatServer to the ChatServerProxy.
 * 
 * @author Christophe TOMBELLE
 */
public class Disconnected extends Signal {
	public Disconnected(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "Disconnected()";
	}
}
