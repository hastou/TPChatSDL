package eu.telecomlille.sdl.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import eu.telecomlille.sdl.Process;

/**
 * TCPLayer est le process frontal d'une interface de programmation pour TCP
 * utilisant le concepts SDL de signaux SDL. Il est utilisable côté serveur
 * ou côté client.
 * 
 * @author Christophe TOMBELLE
 */
// PROCESS TCPLayer;
public class TCPLayer extends Process {
	// DCL addr Charstring;
	private String addr;
	// DCL port integer;
	private int port;
	// DCL sock Socket;
	private Socket sock;

	@Override
	protected void dispatch() {
		System.out.println("TCPLayer : "+_sig);
//	STATE Ready;
//		INPUT TSDUConnect(addr, port);
		if (_sig instanceof TSDUConnect) {
			TSDUConnect sig = (TSDUConnect) _sig;
			addr = sig.address;
			port = sig.port;
//			sock := newSocket(addr, port);
			try {
				InetAddress address = InetAddress.getByName(addr);
				sock = new Socket(address, port);
//			DECISION sock
//			(/= null) :
//				CREATE PConnection(sock, sender);
				String name = sock.getLocalAddress()+":"+sock.getLocalPort();
				create(new PConnection(sock, sender, name));
//				OUTPUT TSDUOpen(sender) TO offspring;
				offspring.add(new TSDUOpen(self, sender));
//				NEXTSTATE -;
//			ELSE
			} catch (IOException e) {
//				OUTPUT TSDUClosed TO sender;
				sender.add(new TSDUClosed(self));
//			ENDDECISION;
			}
//			NEXTSTATE -;
//		INPUT TSDUListen(addr, port);
		} else if (_sig instanceof TSDUListen) {
			TSDUListen signal = (TSDUListen) _sig;
			addr = signal.addr;
			port = signal.port;
//			CREATE PListener(addr, port, sender);
			create(new PListener(addr, port, sender));
//			NEXTSTATE -;
//	ENDSTATE Ready;
		}
	}
// ENDPROCESS TCPLayer;
}
