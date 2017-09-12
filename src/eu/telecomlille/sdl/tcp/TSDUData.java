package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

public class TSDUData extends Signal {
	public String line;
	public TSDUData(IProcess sender, String line) {
		super(sender);
		this.line = line;
	}
	@Override
	public String toString() {
		return "TSDUData('" + line + "')";
	}
}
