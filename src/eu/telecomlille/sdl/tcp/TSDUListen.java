package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUListen extends Signal {
	public String addr;
	public int port;

	public TSDUListen(IProcess sender, String addr, int port) {
		super(sender);
		this.addr = addr;
		this.port = port;
	}

	@Override
	public String toString() {
		return "TSDUListen('" + addr + "', " + port + ")";
	}
}
