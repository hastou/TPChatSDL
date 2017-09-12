package eu.telecomlille.sdlchat.client;

import java.util.HashMap;
import java.util.Map;

import eu.telecomlille.sdl.IProcess;
import eu.telecomlille.sdl.Process;
import eu.telecomlille.sdl.Signal;
import eu.telecomlille.sdl.tcp.*;
import eu.telecomlille.sdlchat.signals.*;

/**
 * Un ChatClient interagit localement avec ce ServerProxy pour interagir avec le
 * ChatServer distant.
 *
 * @author C. TOMBELLE
 */
public class ServerProxy extends Process {
    // PROCESS ServerProxy;
    // DCL client, cnt, dest, from PId;
    protected IProcess client;
    protected IProcess cnt;
    protected IProcess dest;
    protected IProcess from;
    // DCL addr, msg, verb charstring;
    protected String addr;
    protected String msg;
    protected String verb;
    // DCL port integer;
    // DCL d ByteArray_t;

    // Process states implementation
    protected static final int Idle = 0;
    protected static final int Connecting = 1;
    protected static final int Connected = 2;
    // �tat actuel de l'automate
    protected int state;

    // variables li�es � l'encapsulation / d�sencapsulation
    protected static final int NOTHING_PARSED = 0;
    protected static final int DATA_PARSED = 1;
    protected static final int MESSAGE_PARSED = 2;
    protected static final int PRESENCE_PARSED = 3;
    protected static final int ABSENCE_PARSED = 4;
    protected int parsingState = NOTHING_PARSED;
    // registre associant une instance de process et son PId sous forme de
    // cha�ne
    protected HashMap<String, IProcess> hmProc;

    // PId du ChatClient au bout de la route r2
    protected IProcess pidR2;
    // PId de TCPLayer au bout de la route rTCP
    protected IProcess tcp;
    // TODO : ajouter les variables n�cessaires
    protected int _port;


    public ServerProxy(String address, int port) {
        // initialisation du registre des PIds
        hmProc = new HashMap<String, IProcess>();
        // TODO : ajouter ce qui manque... faire ce qu'on fait en g�n�ral dans un constructeur
        client = null;
        cnt = null;
        dest = null;
        from = null;
        addr = address;
        _port = port;
        state = Idle;
        hmProc = new HashMap<>();
        pidR2 = null;
        tcp = null;
    }

    /**
     * D�finit la route vers ChatClient.
     *
     * @param pid Le PId du ChatClient au bout de la route r2.
     */
    public void setR2(IProcess pid) {
        // TODO : � implanter
        pidR2 = pid;
    }

    /**
     * D�finit la route vers TCPLayer.
     *
     * @param pid PId de TCPLayer au bout de la route rTCP.
     */
    public void setRTCP(IProcess pid) {
        // TODO : � implanter
        tcp = pid;
    }

    /**
     * Implantation de la pseudo-transition d'initialisation de ce Process.
     */
    protected void onStart() {
        // TODO : � implanter
        // START;
        // NEXTSTATE Idle;
        state = Idle;
    }

    /**
     * Support pour NEXTSTATE
     *
     * @param state Prochain �tat de l'automate.
     */
    protected void nextState(int state) {
        // TODO : � implanter
        this.state = state;
    }

    /**
     * Aiguiller vers les m�thodes appropri�es selon l'�tat actuel de
     * l'automate.
     */
    protected void dispatch() {
        // TODO : � implanter
        if (state == Idle) {
            onIdle();
        } else if (state == Connecting) {
            onConnecting();
        } else if (state == Connected) {
            onConnected();
        }
    }

    /**
     * R�action de l'automate quand il est dans l'�tat Idle.
     */
    protected void onIdle() {
        // TODO : � implanter
        Object o = _sig;
        if (o instanceof Connect) {
            Connect c = (Connect)o;
            client = c.getSender();
            this.parent.add(new TSDUConnect(this, addr, _port));
            state = Connecting;
        }
//		INPUT Connect;
//			TASK client := sender;
//			TASK addr := getAddress(), port := getPort();
// TODO : rien � faire ici (d�j� fait dans le constructeur)
//			OUTPUT TSDUConnect(addr, port);
//			NEXTSTATE Connecting;
//	ENDSTATE Idle;
    }

    /**
     * R�action de l'automate quand il est dans l'�tat Connecting.
     */
    protected void onConnecting() {
        // TODO : � implanter
        Object o = _sig;
        if (o instanceof TSDUConnected) {
            cnt = sender;
            this.parent.add(new Presence(this, client));
            this.state = Connected;
        } else if (o instanceof TSDUClosed) {
            lblDisc();
            cnt = null;
            this.parent.add(new Absence(this, client));
            this.state = Idle;
        }
//	STATE Connecting;
//		INPUT TSDUConnected;
//			TASK cnt := sender;
//			OUTPUT Presence(client);
//			NEXTSTATE Connected;
//		INPUT TSDUClosed;
//			LABEL lblDisc :
//			TASK cnt := null;
//			OUTPUT Absence(client);
//			NEXTSTATE Idle;
//	ENDSTATE Connecting;
    }

    /**
     * R�action de l'automate quand il est dans l'�tat Connected.
     */
    protected void onConnected() {
        // TODO : � implanter
        Object o = _sig;
        if (_sig instanceof TSDUClosed) {
            lblDisc();
            parent.add(new Disconnected(this));
            this.state = Idle;
        } else if (_sig instanceof TSDUData) {
            TSDUData tsduData = (TSDUData)o;
            parseVerb(tsduData.line);
            if (verb.equals("Data")) {
                msg = unmarshallString(tsduData.line);
                from = unmarshallPId(tsduData.line);
                parent.add(new Data(this, msg, from));
            } else if (verb.equals("Presence")) {
                from = unmarshallPId(tsduData.line);
                parent.add(new Presence(this, from));
            } else if (verb.equals("Absence")) {
                from = unmarshallPId(tsduData.line);
                parent.add(new Absence(this, from));
            }
        } else if (_sig instanceof TSDUClosed) {
            lblDisc();
        } else if (_sig instanceof Data) {
            Data data = (Data)_sig;
            String d = marshallData(data.p1, data.p2);
            cnt.add(new TSDUData(this, d));
        } else if (_sig instanceof Disconnect) {
            cnt.add(new TSDUClose(this));
            this.state = Idle;
        }
//	STATE Connected;
//		INPUT TSDUClosed;
//			LABEL lblDisc :
//			OUTPUT Disconnected;
//			NEXTSTATE Idle;
//		INPUT TSDUData(d);
//			verb := parseVerb(d);
//			DECISION verb;
//			('Data') :
//				msg := unmarshallString(d);
//				from := unmarshallPId(d);
//				OUTPUT Data(msg, from);
//			('Presence') :
//				from := unmarshallPId(d);
//				OUTPUT Presence(from);
//			('Absence') :
//				from := unmarshallPId(d);
//				OUTPUT Absence(from);
//			ELSE
//			ENDDECISION;
//			NEXTSTATE -;
//		INPUT TSDUClosed;
//			JOIN lblDisc
//		INPUT Data(msg, dest);
//			d�:= marshallData('Data', msg, dest);
//			OUTPUT TSDUData(d) TO cnt;
//			NEXTSTATE -;
//		INPUT Disconnect;
//			OUTPUT TSDUClose TO cnt;
//			NEXTSTATE Idle
//	ENDSTATE Connected;
    }

    /**
     * Implantation de l'op�rateur de type de donn�es abstrait marshallData.
     * Cr�e des lignes de texte pour repr�senter le signal Data v�hiculant les
     * param�tres data de type ByteArray_t et dest de type PId.
     *
     * @param data    Implantation du param�tre msg du signal Data
     * @param pidDest Imlantation du param�tre pid du signal Data
     * @return Implantation d'un r�sultat de type charstring compos� des lignes
     * n�cessaires � l'encapsulation d'un signal Data dans un
     * ByteArray_t.
     */
    private String marshallData(String data, IProcess pidDest) {
        String d = "Data\n" + data + "\n" + pidDest.toString();
        return d; // TODO : � modifier pour implantation
    }

    /**
     * Implantation de l'op�rateur de type de donn�es abstrait parseVerb.
     * Analyse la syntaxe des lignes comprises dans data encapsulant le signal �
     * extraire. M�morise ce qu'il faut dans msg et from pour que les op�rateurs
     * de type de donn�es abstrait unmarshallString et unmarshallPId retournent
     * la valeur appropri�e. Met dans verb : "Data", "Absence", "Presence" (ou
     * null si signal non reconnu ou incompl�tement analys�).
     * <p>
     * Si verb non null alors from est positionn� (ainsi que msg si verb =
     * "Data").
     *
     * @param line Implantation d'un param�tre de type ByteArray_t
     * @see #verb
     * @see #from
     * @see #msg
     */
    protected void parseVerb(String line) {
        String strFrom = null;
        switch (parsingState) {
            case NOTHING_PARSED:
                verb = null;
                switch (line) {
                    case "Presence":
                        parsingState = PRESENCE_PARSED;
                        break;
                    case "Data":
                        parsingState = DATA_PARSED;
                        break;
                    case "Absence":
                        parsingState = ABSENCE_PARSED;
                        break;
                }
                break;
            case PRESENCE_PARSED:
                strFrom = line;
                from = new ChatClientProxy(strFrom);
                registerProc(strFrom, from);
                setVerb("Presence");
                break;
            case DATA_PARSED:
                msg = line;
                parsingState = MESSAGE_PARSED;
                break;
            case ABSENCE_PARSED:
                strFrom = line;
                from = getProc(strFrom);
                unregisterProc(from);
                setVerb("Absence");
                break;
            case MESSAGE_PARSED:
                strFrom = line;
                from = getProc(strFrom);
                setVerb("Data");
                break;
            default:
                setVerb(null);
                break;
        }
    }

    protected void setVerb(String str) {
        verb = str;
        parsingState = NOTHING_PARSED;
    }

    /**
     * Implantation de l'op�rateur de type de donn�es abstrait
     * "unmarshallString". Extrait de data le prochain param�tre de type
     * charstring qu'il encapsule.
     *
     * @param data Implantation d'un param�tre de type ByteArray_t encapsulant le
     *             prochain param�tre � extraire.
     * @return Param�tre de type charstring extrait.
     */
    protected String unmarshallString(String data) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) != '\n') {
                s.append(data.charAt(i));
            }
        }
        return s.toString(); // TODO : � modifier pour implantation
    }

    /**
     * Implantation de l'op�rateur de type de donn�es abstrait "unmarshallPId".
     * Extrait de data le prochain param�tre de type PId qu'il encapsule.
     *
     * @param data Message duquel extraire le prochain param�tre.
     * @return Param�tre de type PId extrait.
     */
    protected IProcess unmarshallPId(String data) {
        System.out.println("PID = " + data);
        return null; // TODO : � modifier pour implantation
    }

    /**
     * Implantation de LABEL lblDisc ou JOIN lblDisc
     */
    protected void lblDisc() {
        // TODO : � implanter
        cnt = null;
        parent.add(new Absence(this, client));
        state = Idle;
//	TASK cnt := null;
//	OUTPUT Absence(client);
//	NEXTSTATE Idle;
    }

    /**
     * A des fins de marshalling/unmarshalling de PIds, associe dans un registre
     * un process � son PId sous forme de cha�ne de caract�res.
     *
     * @param strPId Le PId sous forme de cha�ne de caract�res.
     * @param proc   Le process.
     */
    protected void registerProc(String strPId, IProcess proc) {
        // TODO : � implanter
        hmProc.put(strPId, proc);

    }

    /**
     * Gr�ce au registre, obtient le process associ� � la repr�sentation sous
     * forme de cha�ne de caract�res de son PId.
     *
     * @param strPId Le PId sous forme de cha�ne.
     * @return Le process associ�.
     */
    protected IProcess getProc(String strPId) {
        return hmProc.get(strPId); // TODO : � modifier pour implantation
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    /**
     * Fournit la repr�sentation sous forme de cha�ne de caract�res du PId pass�
     * en param�tre.
     *
     * @param proc PId � coder sous forme de caract�res.
     * @return La repr�sentation sous forme de cha�ne de caract�res.
     */
    protected String getPId(IProcess proc) {
        String pid = (String)getKeyFromValue(hmProc, proc);
        return pid; // TODO : � modifier pour implantation
    }

    /**
     * Supprime du registre le process identifi� par un PId sous forme de cha�ne
     * de caract�res.
     *
     * @param strPId Le PId sous forme de cha�ne de caract�res du process �
     *               supprimer du registre.
     */
    protected void unregisterProc(String strPId) {
        hmProc.remove(strPId);
        // TODO : � implanter
    }

    /**
     * Supprime le process du registre.
     *
     * @param proc Le process � supprimer du registre.
     */
    protected void unregisterProc(IProcess proc) {
        // TODO : � implanter
        hmProc.values().remove(proc);
    }

    /**
     * Fournir une repr�sentation du ServerProxy fond�e sur connection d�s que
     * la connexion a eu lieu.
     */
    public String toString() {
        // TODO : utiliser cnt.toString() si disponible

        if (cnt != null) {
            return  cnt.toString();
        }
        return super.toString();
    }
    // ENDPROCESS ServerProxy
}
