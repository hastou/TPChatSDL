package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Disconnection request from the environment to the ChatClient or from a chatClient to the ChatServer.
 * 
 * @author Christophe TOMBELLE
 */
public class Disconnect extends Signal {
	public Disconnect(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "Disconnect()";
	}
}
