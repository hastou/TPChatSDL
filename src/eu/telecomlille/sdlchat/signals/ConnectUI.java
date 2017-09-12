package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Connection request from the environment to the ChatClient.
 * 
 * @author Christophe TOMBELLE
 */
public class ConnectUI extends Signal {
	public ConnectUI(IProcess sender) {
		super(sender);
	}

	@Override
	public String toString() {
		return "ConnectUI()";
	}
}
