package eu.telecomlille.sdlchat.server;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdl.tcp.TSDUConnected;
import eu.telecomlille.sdl.tcp.TSDUListen;
import eu.telecomlille.sdl.tcp.TSDUListening;
import eu.telecomlille.sdl.tcp.TSDUStopped;
import eu.telecomlille.sdlchat.signals.ListenUI;

/**
 * An implementation of TCPServer SDL process.
 */
//PROCESS TCPServer;
public class TCPServer extends Process {
// DCL addr Charstring;
	protected String addr;
// DCL port integer;
	protected int port;
// DCL listener PId;
	protected IProcess listener;
//	route rServ to chatServer
	protected IProcess rServ;
// route rTCP to BTCPLayer
	protected IProcess tcp;
// Process states implementation
	protected static final int Ready = 0;
	protected static final int Listen = 1;
	protected static final int Listening = 2;
	protected int state;

	/**
	 * Construct a instance of an SDL TCPServer process.
	 * @param chatServer The ChatServer
	 * @param port The port to listen to.
	 */
	public TCPServer(IProcess chatServer) {
		this.rServ = chatServer;
	}

	public void setRTCP(IProcess tcp) {
		this.tcp = tcp;
	}

	@Override
	protected void onStart() {
//		START;
//			NEXTSTATE Ready;
		nextState(Ready);
	}

	protected void nextState(int newState) {
		state = newState;
	}

	@Override
	protected void dispatch() {
//		STATE Ready;
		if (state == Ready) {
//			INPUT ListenUI(addr, port);
			if (_sig instanceof ListenUI) {
				ListenUI slsn = (ListenUI) _sig;
				addr = slsn.addr;
				port = slsn.port;
//				OUTPUT TSDUListen(addr, port);
				tcp.add(new TSDUListen(self, addr, port));
//				NEXTSTATE Listen;
				nextState(Listen);
			}
//		ENDSTATE Ready;
//		STATE Listen;
		} else if (state == Listen) {
//			INPUT TSDUListening;
			if (_sig instanceof TSDUListening) {
//				TASK listener := sender;
			listener = sender;
//				NEXTSTATE Listening;
				nextState(Listening);
// 			INPUT TSDUStopped;
			} else if (_sig instanceof TSDUStopped) {
//				OUTPUT ClosedUI;
//				NEXTSTATE(Ready);
				nextState(Ready);
			}
//		ENDSTATE Listen;
//		STATE Listening;
		} else if (state == Listening) {
//			INPUT TSDUConnected;
			if (_sig instanceof TSDUConnected) {
//				CREATE ClientProxy(sender);
				ClientProxy cp = new ClientProxy(sender);
				cp.setR2(rServ);
				offspring = cp;
				offspring.setParent(self, stepper);
//				NEXTSTATE -;
//			INPUT CloseUI
//				OUTPUT TSDUStop TO listener
//				NEXTSTATE -;
//			INPUT TSDUStopped;
			} else if (_sig instanceof TSDUStopped) {
//				OUTPUT ClosedUI
//				NEXTSTATE Ready;
				nextState(Ready);
			}
		}
//		ENDSTATE Listening;
	}
//ENDPROCESS TCPServer;
}
