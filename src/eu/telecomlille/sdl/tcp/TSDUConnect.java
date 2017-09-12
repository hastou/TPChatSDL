package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

/**
 * Connection to the (addr, port) server.
 * @author tombelle
 *
 */
public class TSDUConnect extends Signal {
	public String address;
	public int port;

	public TSDUConnect(IProcess sender, String addr, int port) {
		super(sender);
		this.address = addr;
		this.port = port;
	}

	@Override
	public String toString() {
		return "TSDUConnect(" + address + ", " + port + ")";
	}
}
