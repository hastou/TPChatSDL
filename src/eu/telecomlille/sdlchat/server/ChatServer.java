package eu.telecomlille.sdlchat.server;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.ISignal;
import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdlchat.signals.Absence;
import eu.telecomlille.sdlchat.signals.Connect;
import eu.telecomlille.sdlchat.signals.ConnectedUI;
import eu.telecomlille.sdlchat.signals.Data;
import eu.telecomlille.sdlchat.signals.Disconnect;
import eu.telecomlille.sdlchat.signals.DisconnectUI;
import eu.telecomlille.sdlchat.signals.Disconnected;
import eu.telecomlille.sdlchat.signals.DisconnectedUI;
import eu.telecomlille.sdlchat.signals.Presence;

/**
 * Implements an SDL process for a chat server.
 * 
 * @author C. TOMBELLE
 */
public class ChatServer extends Process {
//PROCESS ChatServer;
//	DCL clientsCount Natural := 0;
	protected int clientsCount;
//	DCL i, tmp Natural;
	protected int i;
	protected int tmp;
//	DCL clients Clients_t;
	protected IProcess clients[] = new IProcess [10];
//	DCL dest PId;
	protected IProcess dest;
//	DCL msg Charstring;
	protected String msg;

	// Implementation
	// state
	protected int state;
	protected static final int Ready = 0;
	// route to env (GUI)
	protected IProcess env;

    public void setEnv(IProcess env) {
    	this.env = env;
    }

	public void output(ISignal sig, IProcess to) {
		to.add(sig);
//		System.out.println("[ChatServer] " + sig + " sent");
	}

	protected void dispatch() {
//		System.out.println("[ChatServer] " + _sig + " received");
//		STATE Ready;
		switch (state) {
		case Ready:
			onReady();
			break;
		}
//ENDPROCESS ChatServer;
	}
	
	protected void onStart() {
//		START;
//		NEXTSTATE Ready;
		nextState(Ready);
	}

	protected void nextState(int state) {
		this.state = state;
	}

	protected void onReady() {
//		INPUT Connect;
	if (_sig instanceof Connect) {
//			OUTPUT ConnectedUI(sender) to env
		output(new ConnectedUI(this, sender), env);
//			TASK tmp := addClient(clients, clientsCount, sender);
		tmp = addClient(clients, sender);
//			TASK i := 0;
		i = 0;
//		lblPres:
//			DECISION i
//		( < clientsCount ) :
		while (i < clientsCount) {
//				DECISION clients(i);
//				( /= sender ):
			if (clients[i] != sender) {
//					OUTPUT Presence(sender) TO clients(i);
				output(new Presence(self, sender), clients[i]);
//					OUTPUT Presence(clients(i) TO sender;
				output(new Presence(self, clients[i]), sender);
			}
//				ELSE:
//				ENDDECISION;
//              TASK i:= i+1;
			i = i + 1;
//			ELSE :
//				JOIN lblPres;
//			ENDDECISION;
		}
//			NEXTSTATE -;
//	INPUT DisconnectUI(disc);
	} else if (_sig instanceof DisconnectUI) {
		DisconnectUI dui = (DisconnectUI) _sig;
		IProcess disc = dui.disc;
//		OUTPUT Disconnected TO disc;
		output(new Disconnected(self), disc);
//		NEXTSTATE -;
//	INPUT Disconnect;
	} else if (_sig instanceof Disconnect) {
//		OUTPUT DisconnectedUI(sender) to env
		output(new DisconnectedUI(this, sender), env);
//			TASK tmp := removeClient(clients, clientsCount, sender);
		tmp = removeClient(clients, sender);
//		TASK i := 0;
		i = 0;
//		lblAbs:
//			DECISION i
//		( < clientsCount ) :
		while (i < clientsCount) {
//				DECISION clients(i);
//				( /= sender ):
			if (clients[i] != sender) {
//					OUTPUT Absence(sender) TO clients(i);
				output(new Absence(self, sender), clients[i]);
			}
//				ELSE:
//				ENDDECISION;
//              TASK i:= i+1;
			i = i + 1;
//			ELSE :
//				JOIN lblAbs;
//			ENDDECISION;
		}
//			NEXTSTATE -;
	}
//
//		INPUT Data(msg, dest);
		else if (_sig instanceof Data) {
			Data dat = (Data) _sig;
			msg = dat.p1;
			dest = dat.p2;
//			DECISION dest;
//			(null):
			if (dest == null) {
//				COMMENT 'dispatch message';
//				TASK i := 0;
				i = 0;
//			lblSend:
//				DECISION i;
//				( < clientsCount ) :
				while (i < clientsCount) {
//					DECISION clients(i);
//					( /= sender ):
					if (clients[i] != sender) {
//						OUTPUT Data(msg, sender) TO clients(i);
						output(new Data(self, msg, sender), clients[i]);
					}
//					ELSE:
//					ENDDECISION;
//                  TASK i:= i+1;
					i = i + 1;
//				ELSE :
//					JOIN lblSend;
//				ENDDECISION;
				}
			}
//			ELSE:
			else {
//				COMMENT 'private message';
//				OUTPUT Data(msg, sender) TO dest;
				output(new Data(self, msg, sender), dest);
//			ENDDECISION;
			}
//			NEXTSTATE -;
		}
//		ENDSTATE Ready;
	}

    /**
	 * Add a client pid to the set of connected client pids.
	 * 
	 * @param array
	 *            An array of client pids.
	 * @param clientsCount
	 *            The number of clients in the array
	 * @param pid
	 *            A pid to add to the set of connected client pids.
	 * @return The new clients count.
	 */
	public int addClient(IProcess array[], IProcess pid) {
		int iLen=array.length, i=0;
		assert clientsCount <= iLen;
		// check presence
		while (i<clientsCount)
			if (array[i++]==pid)
				return clientsCount;
		// grow array if needed
		if (clientsCount == iLen) {
			int size = clientsCount + 10;
			IProcess newClients[] = new IProcess[size];
			for (i=0; i<iLen; i++)
				newClients[i] = array[i];
			array = newClients;
		}
		// add client
		array[clientsCount] = pid;
//		System.out.println("[ChatServer] "+pid+" added to list of connected clients");
		return ++clientsCount;
	}

	/**
	 * Remove a client pid from the set of connected client pids.
	 * 
	 * @param array
	 *            An array of clients PIDs
	 * @param clientsCount
	 *            The number of clients in the array
	 * @param pid
	 *            A pid to remove from the set of connected client pids.
	 * @return The new clients count.
	 */
	public int removeClient(IProcess array[], IProcess pid) {
		assert clientsCount <= array.length;
		int i=0;
		while (i<clientsCount)
			if (array[i++] == pid) {
				int j=i;
				for (; j<clientsCount; j++)
					array[j-1]=array[j];
				clientsCount--;
//				System.out.println("[ChatServer] "+pid+" removed from list of connected clients");
			}
		return clientsCount;
	}
}
