package eu.telecomlille.sdlchat.client;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.ISignal;
import eu.telecomlille.sdl.Stepper;
import eu.telecomlille.sdlchat.signals.ConnectUI;
import eu.telecomlille.sdlchat.signals.ConnectedUI;
import eu.telecomlille.sdlchat.signals.DataUI;
import eu.telecomlille.sdlchat.signals.Disconnect;
import eu.telecomlille.sdlchat.signals.DisconnectedUI;

/**
 * A text user interface with a ChatClient.
 * 
 * @author C. TOMBELLE
 */
public class ChatClientUI implements IProcess {
	protected static int count = 0;
	protected ChatClient _chat;
	protected IProcess connected[] = new IProcess [10];
	protected int iConnected = 0;

	public ChatClientUI() {
		_chat = new ChatClient(null);
		_chat.setEnv(this);
	}
	public ChatClient getChat() {
		return _chat;
	}
	public IProcess getProcess() {
		return this;
	}
	public void connect() {
		_chat.add(new ConnectUI(this));
	}
	public void disconnect() {
		_chat.add(new Disconnect(this));
	}
	public void send(String msg) {
		_chat.add(new DataUI(this, msg, null));
	}
	public void send(String msg, IProcess pid) {
		_chat.add(new DataUI(this, msg, pid));
	}
	public void send(String msg, int iConnected) {
		send(msg, connected[iConnected]);
	}
	public void add(ISignal sig) {
		// display received signals
		if (sig instanceof DataUI) {
			DataUI dui = (DataUI) sig;
			System.out.println("[ChatClientUI] DataUI from "+dui.p2+" to "+dui.getSender()+" : "+dui.p1);
		} else if (sig instanceof ConnectedUI) {
			ConnectedUI cui = (ConnectedUI) sig;
			System.out.println("[ChatClientUI] ConnectedUI "+cui.getSender()+" knows "+cui.p1+" is connected");
			connected[iConnected++] = cui.p1;
		} else if (sig instanceof DisconnectedUI) {
			DisconnectedUI dui = (DisconnectedUI) sig;
			System.out.println("[ChatClientUI] DisconnectedUI "+dui.getSender()+" knows "+dui.p1+" is disconnected");
		}
	}
	public void setParent(IProcess procParent, Stepper s) {
		_chat.setParent(null, s);
	}
}
