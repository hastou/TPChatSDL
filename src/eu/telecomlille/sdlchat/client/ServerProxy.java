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
    // état actuel de l'automate
    protected int state;

    // variables liées à l'encapsulation / désencapsulation
    protected static final int NOTHING_PARSED = 0;
    protected static final int DATA_PARSED = 1;
    protected static final int MESSAGE_PARSED = 2;
    protected static final int PRESENCE_PARSED = 3;
    protected static final int ABSENCE_PARSED = 4;
    protected int parsingState = NOTHING_PARSED;
    // registre associant une instance de process et son PId sous forme de
    // chaîne
    protected HashMap<String, IProcess> hmProc;

    // PId du ChatClient au bout de la route r2
    protected IProcess pidR2;
    // PId de TCPLayer au bout de la route rTCP
    protected IProcess tcp;
    // TODO : ajouter les variables nécessaires
    protected int _port;


    public ServerProxy(String address, int port) {
        // initialisation du registre des PIds
        hmProc = new HashMap<String, IProcess>();
        // TODO : ajouter ce qui manque... faire ce qu'on fait en général dans un constructeur
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
     * Définit la route vers ChatClient.
     *
     * @param pid Le PId du ChatClient au bout de la route r2.
     */
    public void setR2(IProcess pid) {
        // TODO : à implanter
        pidR2 = pid;
    }

    /**
     * Définit la route vers TCPLayer.
     *
     * @param pid PId de TCPLayer au bout de la route rTCP.
     */
    public void setRTCP(IProcess pid) {
        // TODO : à implanter
        tcp = pid;
    }

    /**
     * Implantation de la pseudo-transition d'initialisation de ce Process.
     */
    protected void onStart() {
        // TODO : à implanter
        // START;
        // NEXTSTATE Idle;
        state = Idle;
    }

    /**
     * Support pour NEXTSTATE
     *
     * @param state Prochain état de l'automate.
     */
    protected void nextState(int state) {
        // TODO : à implanter
        this.state = state;
    }

    /**
     * Aiguiller vers les méthodes appropriées selon l'état actuel de
     * l'automate.
     */
    protected void dispatch() {
        // TODO : à implanter
        if (state == Idle) {
            onIdle();
        } else if (state == Connecting) {
            onConnecting();
        } else if (state == Connected) {
            onConnected();
        }
    }

    /**
     * Réaction de l'automate quand il est dans l'état Idle.
     */
    protected void onIdle() {
        // TODO : à implanter
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
// TODO : rien à faire ici (déjà fait dans le constructeur)
//			OUTPUT TSDUConnect(addr, port);
//			NEXTSTATE Connecting;
//	ENDSTATE Idle;
    }

    /**
     * Réaction de l'automate quand il est dans l'état Connecting.
     */
    protected void onConnecting() {
        // TODO : à implanter
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
     * Réaction de l'automate quand il est dans l'état Connected.
     */
    protected void onConnected() {
        // TODO : à implanter
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
//			d := marshallData('Data', msg, dest);
//			OUTPUT TSDUData(d) TO cnt;
//			NEXTSTATE -;
//		INPUT Disconnect;
//			OUTPUT TSDUClose TO cnt;
//			NEXTSTATE Idle
//	ENDSTATE Connected;
    }

    /**
     * Implantation de l'opérateur de type de données abstrait marshallData.
     * Crée des lignes de texte pour représenter le signal Data véhiculant les
     * paramètres data de type ByteArray_t et dest de type PId.
     *
     * @param data    Implantation du paramètre msg du signal Data
     * @param pidDest Imlantation du paramètre pid du signal Data
     * @return Implantation d'un résultat de type charstring composé des lignes
     * nécessaires à l'encapsulation d'un signal Data dans un
     * ByteArray_t.
     */
    private String marshallData(String data, IProcess pidDest) {
        String d = "Data\n" + data + "\n" + pidDest.toString();
        return d; // TODO : à modifier pour implantation
    }

    /**
     * Implantation de l'opérateur de type de données abstrait parseVerb.
     * Analyse la syntaxe des lignes comprises dans data encapsulant le signal à
     * extraire. Mémorise ce qu'il faut dans msg et from pour que les opérateurs
     * de type de données abstrait unmarshallString et unmarshallPId retournent
     * la valeur appropriée. Met dans verb : "Data", "Absence", "Presence" (ou
     * null si signal non reconnu ou incomplètement analysé).
     * <p>
     * Si verb non null alors from est positionné (ainsi que msg si verb =
     * "Data").
     *
     * @param line Implantation d'un paramètre de type ByteArray_t
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
     * Implantation de l'opérateur de type de données abstrait
     * "unmarshallString". Extrait de data le prochain paramètre de type
     * charstring qu'il encapsule.
     *
     * @param data Implantation d'un paramètre de type ByteArray_t encapsulant le
     *             prochain paramètre à extraire.
     * @return Paramètre de type charstring extrait.
     */
    protected String unmarshallString(String data) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) != '\n') {
                s.append(data.charAt(i));
            }
        }
        return s.toString(); // TODO : à modifier pour implantation
    }

    /**
     * Implantation de l'opérateur de type de données abstrait "unmarshallPId".
     * Extrait de data le prochain paramètre de type PId qu'il encapsule.
     *
     * @param data Message duquel extraire le prochain paramètre.
     * @return Paramètre de type PId extrait.
     */
    protected IProcess unmarshallPId(String data) {
        System.out.println("PID = " + data);
        return null; // TODO : à modifier pour implantation
    }

    /**
     * Implantation de LABEL lblDisc ou JOIN lblDisc
     */
    protected void lblDisc() {
        // TODO : à implanter
        cnt = null;
        parent.add(new Absence(this, client));
        state = Idle;
//	TASK cnt := null;
//	OUTPUT Absence(client);
//	NEXTSTATE Idle;
    }

    /**
     * A des fins de marshalling/unmarshalling de PIds, associe dans un registre
     * un process à son PId sous forme de chaîne de caractères.
     *
     * @param strPId Le PId sous forme de chaîne de caractères.
     * @param proc   Le process.
     */
    protected void registerProc(String strPId, IProcess proc) {
        // TODO : à implanter
        hmProc.put(strPId, proc);

    }

    /**
     * Grâce au registre, obtient le process associé à la représentation sous
     * forme de chaîne de caractères de son PId.
     *
     * @param strPId Le PId sous forme de chaîne.
     * @return Le process associé.
     */
    protected IProcess getProc(String strPId) {
        return hmProc.get(strPId); // TODO : à modifier pour implantation
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
     * Fournit la représentation sous forme de chaîne de caractères du PId passé
     * en paramètre.
     *
     * @param proc PId à coder sous forme de caractères.
     * @return La représentation sous forme de chaîne de caractères.
     */
    protected String getPId(IProcess proc) {
        String pid = (String)getKeyFromValue(hmProc, proc);
        return pid; // TODO : à modifier pour implantation
    }

    /**
     * Supprime du registre le process identifié par un PId sous forme de chaîne
     * de caractères.
     *
     * @param strPId Le PId sous forme de chaîne de caractères du process à
     *               supprimer du registre.
     */
    protected void unregisterProc(String strPId) {
        hmProc.remove(strPId);
        // TODO : à implanter
    }

    /**
     * Supprime le process du registre.
     *
     * @param proc Le process à supprimer du registre.
     */
    protected void unregisterProc(IProcess proc) {
        // TODO : à implanter
        hmProc.values().remove(proc);
    }

    /**
     * Fournir une représentation du ServerProxy fondée sur connection dès que
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
