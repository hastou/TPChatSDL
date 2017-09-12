package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Absence of a ChatClient.
 * 
 * @author Christophe TOMBELLE
 */
public class Absence extends Signal {
	public IProcess p1;
	/**
	 * @param sender
	 *            The pid of the sender (the server).
	 * @param p1
	 *            The pid of a former ChatClient.
	 */
	public Absence(IProcess sender, IProcess p1) {
		super(sender);
		this.p1 = p1;
	}
	public String toString() {
		return "Absence("+p1+")";
	}
}
