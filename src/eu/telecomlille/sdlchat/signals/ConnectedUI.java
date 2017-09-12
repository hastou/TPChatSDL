package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Connection indication from the ChatServer or the ChatClient to its environment.
 * 
 * @author Christophe TOMBELLE
 */
public class ConnectedUI extends Signal {
	public IProcess p1;

	public ConnectedUI(IProcess sender, IProcess p1) {
		super(sender);
		this.p1 = p1;
	}

	public String toString() {
		return "ConnectedUI(" + p1 + ")";
	}
}
