package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Un process PListener est instanci� par TCPLayer c�t� serveur pour attendre
 * des connexions sur une adresse et un port d'�coute sp�cifi�s en param�tres
 * formels du process ; le 3�me param�tre formel user d�signe l'utilisateur �
 * qui doivent �tre signal�s le d�marrage (par un TSDUListening) et la fin (par
 * un TSDUStopped) de l'�coute. Un PListener cr�e un PConnection � chaque fois
 * qu'un client TCP se connecte. C'est la PConnection qui se signale elle-m�me �
 * l'utilisateur.
 */
public class PListener extends Process {
	// TODO : � implanter
// FPAR addr Charstring;
	protected String addr;
// FPAR port integer;
	protected int port;
// FPAR user PId;
	protected IProcess _user;
// DCL sock, sockExch Socket;
	protected ServerSocket _sock;
    protected Socket _sock_Exch;

	public PListener(String addr, int port, IProcess user) {
		// TODO : Initialiser les param�tres formels (FPAR) du process
		this.port = port;
		_user = user;
		this.addr = addr;
	}

	/**
	 * Cette m�thode est appel�e quand le thread de ce process d�marre, avant
	 * que la pr�sence d'un signal soit recherch� en t�te de fifo (implantation
	 * de la pseudo-transition d'initialisation).
	 */
	@Override
	protected void onStart() {
		// TODO : � implanter
		try {
			_sock = new ServerSocket(port);
			startListening();
			_user.add(new TSDUListening(this));
		} catch (IOException e) {
			e.printStackTrace();
			lblStop();
		}
//	START:
//		TASK sock := newListen(addr, port)
//			TODO : cr�er la socket d'�coute
//			DECISION sock
//			(/= null) :
//	 			TASK error := startListening()
//				OUTPUT TSDUListening to user;
//				NEXTSTATE Ready;
//			ELSE
//				LABEL lblStop :
//			ENDDECISION;
	}

	/**
	 * Cette m�thode est appel�e quand un signal disponible dans _sig a �t�
	 * pr�lev� en t�te de Fifo. Comme PListener n'a qu'un seul �tat, il suffit
	 * donc de r�agir selon la nature du signal.
	 */
	@Override
	protected void dispatch() {
		System.out.println("PListener : "+_sig);

		Object o = _sig;
		if (o instanceof Signal) {
		    Signal s = (Signal)o;
		    if (s instanceof TCPConnected) {
		        TCPConnected tcpConnected = (TCPConnected)s;
                PConnection connection = new PConnection(_sock_Exch, _user, tcpConnected.toString());
            } else if (s instanceof TSDUStop) {
                try {
                    _sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (s instanceof TCPClosed) {
		        lblStop();
            }
        }

//	TODO : � implanter
//	STATE Ready;
//		INPUT TCPConnected(sockExch);
//			CREATE PConnection(sockExch, user)
//			TODO : utiliser name comme 3�me param�tre de PConnection (hors mod�le)
//			NEXTSTATE -;
//		INPUT TSDUStop;
//			TASK error := close(sock);
//			TODO : clore la socket d'�coute
//			NEXTSTATE -;
//		INPUT TCPClosed;
//			JOIN lblStop;
//	ENDSTATE Ready;
	}

	/**
	 * Implantation de LABEL lblStop ou JOIN lblStop.
	 */
	protected void lblStop() {
		_user.add(new TSDUStopped(this));
		_bRun=false;
		// TODO : � implanter
//		OUTPUT TSDUStopped TO user;
//		STOP;
	}

	/**
	 * D�marre le thread d�di� � l'attente des connexions TCP. Implanter le
	 * comportement de ce thread : d�poser un TCPConnected ou un TCPCLosed dans
	 * la fifo de ce PListener selon qu'une connexion a eu lieu ou que la socket
	 * d'�coute a �t� ferm�e.
	 */
	protected void startListening() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (_bRun) {
					try {
						Socket sock = _sock.accept();
						PListener.this.add(new TCPConnected(PListener.this, sock));
					} catch (IOException e) {
						e.printStackTrace();
						PListener.this.add(new TCPClosed(PListener.this));
						_bRun = false;
					}
				}
				// TODO : Boucler sur l'acceptation des connexions sur la socket
				// TODO serveur. Un TCPConnected(sock) doit �tre mis dans la
				// TODO fifo � chaque fois qu'un client TCP se connecte. Si la
				// TODO socket serveur est ferm�e (IOException) la boucle doit
				// TODO se terminer et un TCPClosed doit �tre mis dans la fifo.
			}
		}).start();
	}

	/**
	 * Une instance de ce signal interne doit �tre mis dans la fifo de ce
	 * PListener � chaque fois qu'un client TCP se connete. Le sender est self
	 * et le param�tre sock correspond est la socket d'�change.
	 */
	protected class TCPConnected extends Signal {
		public Socket sock;
		public TCPConnected(IProcess sender, Socket sock) {
			super(sender);
			this.sock = sock;
		}

		@Override
		public String toString() {
			return "TCPConnected()";
		}
	}
	
	/**
	 * Une instance de ce signal interne doit �tre mis dans la fifo de ce
	 * PListener quand la socket serveur est ferm�e. Le sender est self.
	 */
	protected class TCPClosed extends Signal {
		public TCPClosed(IProcess sender) {
			super(sender);
		}

		@Override
		public String toString() {
			return "TCPClosed()";
		}
	}

	@Override
	public String toString() {
		return "PListener('" + addr + "', " + port + ")";
	}
}
