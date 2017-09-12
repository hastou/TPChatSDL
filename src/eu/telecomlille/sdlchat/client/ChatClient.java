package eu.telecomlille.sdlchat.client;

import eu.telecomlille.sdl.Fifo;
import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.ISignalListener;
import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdlchat.signals.Absence;
import eu.telecomlille.sdlchat.signals.Connect;
import eu.telecomlille.sdlchat.signals.ConnectUI;
import eu.telecomlille.sdlchat.signals.ConnectedUI;
import eu.telecomlille.sdlchat.signals.Data;
import eu.telecomlille.sdlchat.signals.DataUI;
import eu.telecomlille.sdlchat.signals.Disconnect;
import eu.telecomlille.sdlchat.signals.DisconnectedUI;
import eu.telecomlille.sdlchat.signals.Presence;

/**
 * Implements an SDL process for a chat client.
 * 
 * @author C. TOMBELLE
 */
public class ChatClient extends Process {
// PROCESS ChatClient;
//	DCL msg charstring;
	protected String msg;
//	DCL orig, dest PId;
	protected IProcess orig;
	protected IProcess dest;
	// Implementation
	// states
	protected int state;
	protected static final int Idle = 0;
	protected static final int Connected = 1;
	// route to env (GUI)
	protected ISignalListener env;
	// route to ServerProxy
	protected IProcess pidR2;
	protected String name;

	public ChatClient(String name) {
		this.name = name;
		self = this;
        _fifo = new Fifo();
        sender = null;
	}

	public void setR2Process(IProcess pidR2) {
		this.pidR2 = pidR2;
	}

	public void setEnv(IProcess proc) {
		env = proc;
	}

	protected void dispatch() {
		switch (state) {
		case Idle :
			onIdle();
			break;
		case Connected :
			onConnected();
			break;
		}
	}

//	START ;
	protected void onStart() {
//			NEXTSTATE Idle;
		nextState(Idle);
	}

	protected void nextState(int state) {
		this.state = state;
	}

	protected void onIdle() {
//	STATE Idle;
//		INPUT ConnectUI;
		if (_sig instanceof ConnectUI) {
//			OUTPUT Connect;
			pidR2.add(new Connect(this));
//			NEXTSTATE Connected;
			nextState(Connected);
		}
//	ENDSTATE Idle;
	}

	protected void onConnected() {
//	STATE Connected;
//		INPUT Presence(orig);
		if (_sig instanceof Presence) {
			Presence pres = (Presence) _sig;
			orig = pres.p1;
//			OUTPUT ConnectedUI(orig);
			env.add(new ConnectedUI(self, orig));
//			NEXTSTATE -;
//		INPUT Absence(orig);
		} else if (_sig instanceof Absence) {
			Absence abs = (Absence) _sig;
			orig = abs.p1;
//			OUTPUT DisconnectedUI(orig);
			env.add(new DisconnectedUI(self, orig));
//			DECISION orig
//			(self) :
			if (orig == self)
//				NEXTSTATE Idle;
				nextState(Idle);
//			ELSE
//				COMMENT 'stay connected'
//				NEXTSTATE -;
//			ENDDECISION;
		}
//		INPUT DataUI(msg, dest);
		else if (_sig instanceof DataUI) {
			DataUI dui = (DataUI) _sig;
			msg = dui.p1;
			dest = dui.p2;
//			OUTPUT Data(msg, dest);
			pidR2.add(new Data(self, msg, dest));
//			NEXTSTATE -;
		}
//		INPUT Data(msg, orig);
		else if (_sig instanceof Data) {
			Data data = (Data) _sig;
			msg = data.p1;
			orig = data.p2;
//			OUTPUT DataUI(msg, orig);
			env.add(new DataUI(self, msg, orig));
//			NEXTSTATE -;
		}
//		INPUT Disconnect;
		else if (_sig instanceof Disconnect) {
//			OUTPUT Disconnect;
			pidR2.add(new Disconnect(self));
//			OUTPUT DisconnectedUI(self);
			env.add(new DisconnectedUI(self, self));
//			NEXTSTATE Idle;
			nextState(Idle);
		}
//	ENDSTATE Connected;
	}

	@Override
	public String toString() {
		if (name != null)
			return name;
		if (pidR2 == null)
			super.toString();
		return pidR2.toString();
	}
	// ENDPROCESS ChatClient;
}
