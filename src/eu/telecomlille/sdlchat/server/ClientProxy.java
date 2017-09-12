package eu.telecomlille.sdlchat.server;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.ISignal;
import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdl.Registry;
import eu.telecomlille.sdl.tcp.TSDUClose;
import eu.telecomlille.sdl.tcp.TSDUClosed;
import eu.telecomlille.sdl.tcp.TSDUData;
import eu.telecomlille.sdl.tcp.TSDUOpen;
import eu.telecomlille.sdlchat.signals.Absence;
import eu.telecomlille.sdlchat.signals.Connect;
import eu.telecomlille.sdlchat.signals.Data;
import eu.telecomlille.sdlchat.signals.Disconnect;
import eu.telecomlille.sdlchat.signals.Disconnected;
import eu.telecomlille.sdlchat.signals.Presence;

/**
 * An implementation of ClientProxy SDL process.
 */
//PROCESS ClientProxy;
public class ClientProxy extends Process {
//  FPAR cnt PId;
	protected IProcess cnt;

//	DCL msg, verb Charstring;
	protected String msg;
	protected String verb;
//	DCL from, dest PId;
	protected IProcess from, dest;
//	DCL d Charstring;
	protected String d;

//  route to server;
	protected IProcess server;

	// parsing state
	protected static final int NOTHING_PARSED = 0;
	protected static final int DATA_PARSED = 1;
	protected static final int MESSAGE_PARSED = 2;
	protected int parsingState = NOTHING_PARSED;
	protected String tmpVerb;

	public ClientProxy(IProcess cnt) {
		this.cnt = cnt;
	}

	public void setR2(IProcess pid) {
		server = pid;
	}

	protected void onStart() {
//		START;
		// (not in the model) add self to ClientProxies registry
		Registry.INSTANCE.registerProc(toString(), self);
//			OUTPUT Connect;
		server.add(new Connect(self));
//			OUTPUT TSDUOpen(self) TO cnt;
		cnt.add(new TSDUOpen(self, self));
//			NEXTSTATE Ready;
	}

	public void output(ISignal sig, IProcess to) {
		to.add(sig);
//		System.out.println("[ClientProxy] " + sig + " sent");
	}

	protected void dispatch() {
		System.out.println("[ClientProxy] " + _sig + " received");
//		STATE Ready;
//			INPUT TSDUData(d);
		if (_sig instanceof TSDUData) {
			TSDUData tsd = (TSDUData) _sig;
			d = tsd.line;
			System.out.println("TSDUData("+d+") received");
//				DECISION unmarshallVerb(d);
			unmarshallVerb(d);
//				('Data')
			if ("Data".equals(verb)) {
//					msg := unmarshallString();
//					dest := unmarshallPId();
//					OUTPUT Data(msg, dest);
				server.add(new Data(self, msg, dest));
//				ELSE
//				ENDDECISION;
			}
//				NEXTSTATE -;
//			INPUT TSDUClosed;
		} else if (_sig instanceof TSDUClosed) {
				// (not in the model) remove self from ClientProxies registry
			Registry.INSTANCE.unregisterProc(self.toString());
//				OUTPUT Disconnect;
			output(new Disconnect(self), server);
//				STOP;
			stop();
//			INPUT Data(msg, from);
		} else if (_sig instanceof Data) {
			Data dat = (Data) _sig;
			msg = dat.p1;
			from = dat.p2;
//				d := marshallData(msg, from);
			d = marshallData(msg, from);
//					OUTPUT TSDUData(d);
			cnt.add(new TSDUData(self, d));
			System.out.println("TSDUData("+d+") emitted");
//					NEXTSTATE -;
//				INPUT Disconnected;
		} else if (_sig instanceof Disconnected) {
//					OUTPUT TSDUClose;
			output(new TSDUClose(self), cnt);
//					NEXTSTATE -;
//				INPUT Presence(from);
		} else if (_sig instanceof Presence) {
			Presence pres = (Presence) _sig;
			from = pres.p1;
//					d := marshallPresence(from);
			d = marshallPresence(from);
//					OUTPUT TSDUData(d);
			cnt.add(new TSDUData(self, d));
//					NEXTSTATE -;
//				INPUT Absence(from);
		} else if (_sig instanceof Absence) {
			Absence abs = (Absence) _sig;
			from = abs.p1;
//					d := marshallAbsence(from);
			d = marshallAbsence(from);
//					OUTPUT TSDUData(d);
			cnt.add(new TSDUData(self, d));
//					NEXTSTATE -;
		}
//		ENDSTATE Ready;
	}

//	NEWTYPE Marshalling;
//		operators
//			marshallAbsence: PId -> Charstring;
	protected String marshallAbsence(IProcess pidFrom) {
		if (pidFrom == null)
			return null;
		return "Absence\n"+pidFrom.toString();
	}

//			marshallData: Charstring, PId -> Charstring;
	protected String marshallData(String strMsg, IProcess pidFrom) {
		String strFrom = pidFrom == null ? "null" : pidFrom.toString();
		if (Registry.INSTANCE.getProc(strFrom) == null)
			return null;
		return "Data\n"+strMsg+"\n"+strFrom;
	}

//			marshallPresence: PId -> Charstring;
	protected String marshallPresence(IProcess pidFrom) {
		if (pidFrom == null)
			return null;
		String strFrom = pidFrom.toString();
		if (Registry.INSTANCE.getProc(strFrom) == null)
			return null;
		return "Presence\n"+strFrom;
	}

//			unmarshallVerb: Charstring -> Charstring;
	protected void unmarshallVerb(String line) {
		String [] lines = line.split("\n");
		int iLine = 0;
		while (iLine < lines.length) {
			String strDest = null;
			switch (parsingState) {
			case NOTHING_PARSED :
				verb = null;
				tmpVerb = lines[iLine];
				if ("Data".equals(tmpVerb))
					parsingState = DATA_PARSED;
				break;
			case DATA_PARSED :
				msg = lines[iLine];
				parsingState = MESSAGE_PARSED;
				break;
			case MESSAGE_PARSED :
				strDest = lines[iLine];
				dest = Registry.INSTANCE.getProc(strDest);
				setVerb();
				break;
			default:
				tmpVerb = null;
				setVerb();
				break;
			}
			iLine++;
		}
	}

	protected void setVerb() {
		verb = tmpVerb;
		parsingState = NOTHING_PARSED;
	}
//			unmarshallMsg: Charstring -> Charstring;
//			unmarshallPId: -> Charstring;
//	ENDNEWTYPE Marshalling;

	/**
	 * Return an "address:port" string.
	 * @return The "address:port" string.
	 * @see Object#toString()
	 */
	public String toString() {
		return cnt.toString();
	}
//ENDPROCESS ClientProxy;
}
