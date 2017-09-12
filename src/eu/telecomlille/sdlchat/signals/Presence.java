package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Presence of another ChatClient.
 * 
 * @author Christophe TOMBELLE
 */
public class Presence extends Signal {
	public IProcess p1;

	/**
	 * 
	 * @param sender
	 *            The pid of the sender (the server).
	 * @param p1
	 *            The pid of another ChatClient.
	 */
	public Presence(IProcess sender, IProcess p1) {
		super(sender);
		this.p1 = p1;
	}
	public String toString() {
		return "Presence("+p1+")";
	}
}
