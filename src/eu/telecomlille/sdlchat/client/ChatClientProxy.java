package eu.telecomlille.sdlchat.client;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.ISignal;
import eu.telecomlille.sdl.Stepper;

/**
 * A proxy for another ChatClient connected to the ChatServer.
 * Useful to unmarshall the PId from a Presence signal which
 * denotes another ChatClient.
 * 
 * @author C. TOMBELLE
 */
public class ChatClientProxy implements IProcess {
	protected String strPId;
	public ChatClientProxy(String strPId) {
		this.strPId = strPId;
	}
	@Override
	public void add(ISignal oSignal) {
	}
	@Override
	public void setParent(IProcess procParent, Stepper s) {
	}
	@Override
	public String toString() {
		return strPId;
	}
}
