package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Disconnection request from the environment to the ChatServer.
 * 
 * @author Christophe TOMBELLE
 */
public class DisconnectUI extends Signal {
	public IProcess disc;

	public DisconnectUI(IProcess sender, IProcess disc) {
		super(sender);
		this.disc = disc;
	}

	@Override
	public String toString() {
		return "DisconnectUI(" + disc + ")";
	}

}
