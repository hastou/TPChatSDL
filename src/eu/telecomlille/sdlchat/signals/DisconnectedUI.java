package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Disconnection indication from the ChatServer or the ChatClient to its environment.
 * 
 * @author Christophe TOMBELLE
 */
public class DisconnectedUI extends Signal {
	public IProcess p1;
	public DisconnectedUI(IProcess sender, IProcess p1) {
		super(sender);
		this.p1 = p1;
	}
	public String toString() {
		return "DisconnectedUI("+p1+")";
	}
}
