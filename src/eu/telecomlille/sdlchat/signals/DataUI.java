package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Data to/from a ChatClient from/to the ChatServer.
 * 
 * @author Christophe TOMBELLE
 */
public class DataUI extends Signal {
	public String p1;
	public IProcess p2;

	/**
	 * 
	 * @param sender
	 *            The pid of the sender that may be a client or the server.
	 * @param p1
	 *            The message as a string.
	 * @param p2
	 *            If sender is the UI, p2 is the pid of another client this
	 *            message is for or is null if the message is for all other
	 *            clients. If sender is a client, p2 is the pid of the client
	 *            that sent the message to the server.
	 */
	public DataUI(IProcess sender, String p1, IProcess p2) {
		super(sender);
		this.p1 = p1;
		this.p2 = p2;
	}

	public String toString() {
		return "DataUI(" + p1 + ", " + p2 + ")";
	}
}
