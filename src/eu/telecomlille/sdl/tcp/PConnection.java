package eu.telecomlille.sdl.tcp;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdl.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Un process PConnection est instanci� par TCPLayer c�t� client ou par
 * PListener c�t� serveur ; le param�tre formel sock est la socket d'�change
 * d'une connexion d�j� �tablie avec l'entit� paire ; le param�tre formel user
 * est le process utilisateur auquel la PConnection se signale par un
 * TSDUConnected quand elle est instanci�e (dans la pseudo-transition). Le
 * param�tre du signal TSDUOpen est le PId du process local prenant en charge
 * la r�ception des TSDUData.
 */
public class PConnection extends Process {
//PROCESS PConnection;
//	FPAR sock Socket;
//	FPAR user PId;
//	DCL line Charstring;
//	DCL peer PId;
//	DCL error Integer;

	// variable hors mod�le retourn�e par la m�thode toString.
	protected String strToString;
	// variables hors mod�le li�es � sock ; leur donner une valeur lors de
	// l'initialisation du process.
	private PrintWriter _pw;
	private BufferedReader _br;
	private Socket _sock;
	private IProcess _user;

	private IProcess _peer;

	/**
	 * Cr�er une instance de process PConnection ; la cr�ation au sens SDL doit
	 * �tre compl�t�e par un appel � {@link #setParent(IProcess, eu.telecomlille.sdl.Stepper)}.
	 * 
	 * @param sock
	 *            Valeur pour FPAR sock Socket
	 * @param user
	 *            Valeur pour FPAR user PId
	 * @param name
	 *            Valeur hors mod�le � retourner par la m�thode toString.
	 */
	public PConnection(Socket sock, IProcess user, String name) {
		strToString = name;
		// TODO : � compl�ter : donner leurs valeurs aux param�tre formels (FPARs)
		_sock = sock;
		_user = user;
		_peer = null;
	}

	/**
	 * Cette m�thode est appel�e quand le thread de ce process d�marre, avant
	 * que la pr�sence d'un signal soit recherch� en t�te de fifo (implantation
	 * de la pseudo-transition d'initialisation).
	 */
	protected void onStart() {
		// TODO : donner une valeur � _pw et _br � partir de sock.
		try {
			_pw = new PrintWriter(_sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			_br = new BufferedReader(new InputStreamReader(_sock.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO : � implanter
		//	START;
		//		OUTPUT TSDUConnected to user;
		//			NEXTSTATE Ready;
		_user.add(new TSDUConnected(this));
	}

	/**
	 * Cette m�thode est appel�e quand un signal disponible dans _sig a �t�
	 * pr�lev� en t�te de Fifo. Comme PConnection n'a qu'un seul �tat, il suffit
	 * donc de r�agir selon la nature du signal.
	 */
	@Override
	protected void dispatch() {
		System.out.println("PConnection : "+_sig);
		Object o = _sig;
		if (o instanceof Signal) {
			Signal s = (Signal)o;
			if (s instanceof TSDUData) {
				TSDUData tsduData = (TSDUData)s;
				_pw.write(tsduData.line);
			} else if (s instanceof TCPData) {
				TCPData tcpData = (TCPData)s;
				if (_peer != null) {
					_peer.add(new TSDUData(this, tcpData.line));
				}
			} else if (s instanceof TSDUOpen) {
				TSDUOpen tsduOpen = (TSDUOpen)s;
				_peer = tsduOpen.peer;
				startReception();
			} else if (s instanceof TSDUClose) {
				try {
					_sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				_bRun = false;
			} else if (s instanceof TCPClosed) {
				if (_peer != null) {
					_peer.add(new TSDUClosed(this));
				}
			}
		}

//		STATE Ready;
//			INPUT TSDUData(line);
//				OUTPUT TCPData(line);
//				TODO : envoyer line sur _pw
//				NEXTSTATE -;
//			INPUT TCPData(line);
//				OUTPUT TSDUData(line) TO peer;
//				NEXTSTATE -;
//			INPUT TSDUOpen(peer);
//			TASK error := startReception();
//				NEXTSTATE -;
//			INPUT TSDUClose;
//				TASK error := close(sock)
//				TODO : clore la socket sock
//				NEXTSTATE -;
//			INPUT TCPClosed;
//				OUTPUT TSDUClosed to peer;
//				STOP;
//		ENDSTATE Ready;
	}

	/**
	 * Implantation de l'op�rateur de type de donn�es abstrait startReception
	 * qui d�marre le thread de r�ception.
	 */
	protected void startReception() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (_bRun) {
					try {
						String line = _br.readLine();
						PConnection.this._fifo.add(new TCPData(PConnection.this, line));
					} catch (IOException e) {
						System.out.println("PConnection : Socket closed");
						try {
							_sock.close();
							PConnection.this.add(new TCPClosed(PConnection.this));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					// TODO : Boucler sur la lecture d'une ligne � partir du
					// TODO BufferedReader. Pour chaque ligne lue, un TCPData(line)
					// TODO doit �tre mis dand la fifo de ce PConnection si la
					// TODO socket est ferm�e (IOException) la boucle doit se
					// TODO terminer et un TCPClosed doit �tre mis dans la fifo de
					// TODO cette PConnection.
				}
			}
		}).start();
	}

	@Override
	public String toString() {
		return strToString;
	}

	/**
	 * Une instance de ce signal interne doit �tre mis dans la fifo de ce
	 * PConnection � chaque fois qu'une ligne est lue depuis le BufferedReader.
	 */
	protected class TCPData extends Signal {
		protected String line;

		public TCPData(IProcess sender, String line) {
			super(sender);
			this.line = line;
		}

		@Override
		public String toString() {
			return "TCPData('" + line + "')";
		}
	}

	/**
	 * Une instance de ce signal interne doit �tre mis dans la fifo de ce
	 * PConnection quand la socket est ferm�e.
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

}
