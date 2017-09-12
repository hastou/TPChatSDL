package eu.telecomlille.sdlchat.signals;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Request from the environment to the TCPServer to listen to a given port.
 * 
 * @author Christophe TOMBELLE
 */
public class ListenUI extends Signal {
	public String addr;
	public int port;

	public ListenUI(IProcess sender, String addr, int port) {
		super(sender);
		this.addr = addr;
		this.port = port;
	}

	@Override
	public String toString() {
		return "ListenUI(" + port + ")";
	}
}
