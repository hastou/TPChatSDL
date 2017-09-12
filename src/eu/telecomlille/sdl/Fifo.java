package eu.telecomlille.sdl;

/**
 * A fifo class with save capacity.
 * In the context of this Fifo, elements stored in it are called signals.
 *
 * You can queue  a signal to the fifo with the addSignal method.
 * You can know if it's empty with the isEmpty method.
 * You can take out the head signal with the get method.
 *
 * Saving a signal consists in introducing an offset at the head of the fifo,
 * so that the signal can't be removed from the fifo until the savings
 * are cancelled.
 * You can save head signals of the specified class by calling the 
 * save( aSignalClass ) method.
 * You can also save all head Integer signals having the specified int value
 * by calling save ( anInt ).
 * You can reset all savings by calling save( null ). The head of the fifo
 * returns back to its initial position, so that saved signals are again
 * accessible.
 * Signals got from the Fifo are not signal clones but original signals.
 * In a multithreaded environment, the Fifo gives no guarantees that the
 * signal is not beeing modified from an another thread.
 * None of this Fifo methods are synchronized. It's the caller's issue to
 * ensure proper synchronization.
 *
 * @author: C. Tombelle
 */
import java.util.Vector;

public class Fifo {
    protected Vector _vFifo = new Vector();
    protected int _iHead = 0;
    /**
     * The Fifo default constructor.
     */
    public Fifo() {
	super();
    }
    /**
     * Queues an Integer signal to the fifo.
     * Creation date: (24/09/01 10:25:30)
     * 
     * @param iSignal int is the value of the queued signal.
     */
    public void add( int iSignal ) {
	_vFifo.add( new Integer( iSignal ) );
    }
    /**
     * Queues a signal to the Fifo. Any class may be used but primitive types.
     * For primitive types wrapper classes should be used such as Integer for
     * int, Character for char and so on.
     * 
     * Creation date: (20/09/01 14:15:29)
     * @param param java.lang.Object is the signal to be queued.
     */
    public void add( Object signal ) {
	if (signal != null)
	    _vFifo.add( signal );
    }
    /**
     * Returns and removes a signal from the fifo head.
     * Signal got from the Fifo is not a signal clone but the original
     * signal. In a multithreaded environment, the Fifo gives no
     * guarantees that the signal is not beeing modified from an
     * another thread.
     * 
     * Creation date: (20/09/01 13:57:38)
     * @return java.lang.Object is the removed signal.
     */
    public Object get() {
	Vector vFifo = _vFifo;
	if (vFifo.isEmpty())
	    return null;
	Object oSignal = vFifo.get( getHead() );
	vFifo.remove( getHead() );
	return oSignal;
    }
    /**
     * Returns an array of all signals present in the Fifo storage
     * regardless they are saved or not. Saved signal are stored from
     * the 0 index. The getHead() method returns the index of the 1st
     * non saved signal.
     * This method is provided only for debugging purpose and should not
     * be used for normal operation because it doesn't match traditional
     * fifo use. The sum of getSize() and getHead() normally matches the
     * returned object array length.
     * Signals referred in the returned object array are not signal clones
     * but original signals. In a multithreaded environment, the Fifo
     * gives no guarantees that the signal is not beeing modified.
     *
     * Creation date: (21/09/01 08:25:32)
     * @see #getHead #getSize
     * @return java.lang.Object[] is an Object array with the whole Fifo
     * content.
     */
    public Object[] getContent() {
	return _vFifo.toArray();
    }
    /**
     * Returns the position of the 1st accessible (non saved) signal.
     *
     * Creation date: (21/09/01 08:34:39)
     * @see #getContent
     * @return int the position of the 1st accessible signal.
     */
    public int getHead() {
	return _iHead;
    }
    /**
     * Returns the number of accessible (non saved) signals.
     * 
     * Creation date: (24/09/01 10:15:56)
     * @return int the number of accessible signals.
     */
    public int getSize() {
	return _vFifo.size() - _iHead;
    }
    /**
     * Tells wheter accessible (non saved) signals exist or not.
     * 
     * Creation date: (21/09/01 08:50:59)
     * @return boolean true if no accessible signals exist.
     */
    public boolean isEmpty() {
	return getSize() == 0;
    }
    /**
     * Saves all head signals of the specified int value.
     * All head Integer( iSignal ) signals will be saved.
     * 
     * Creation date: (24/09/01 10:01:39)
     * @param iSignal int The int value of Integer signals to save.
     */
    public void save( int iSignal ) {
	Vector vFifo=_vFifo;
	int i=getHead();
	int iCount=getSize()+getHead();
	boolean bLoop=true;
	while (i<iCount && bLoop) {
	    Object oSignal = vFifo.get(i);
	    if (oSignal instanceof Integer && ((Integer)oSignal).intValue()==iSignal)
		i++;
	    else
		bLoop=false;
	}
	_iHead=i;
    }
    /**
     * Saves all head signals of the specified class. May also be used
     * to reset savings, by using a null argument.
     * 
     * Creation date: (20/09/01 14:29:03)
     * @param clsSignal java.lang.Class is the class of the signals that
     * must be saved. If null, resets all savings effects.
     */
    public void save( Class clsSignal ) {
	if (clsSignal==null) {
	    _iHead=0;
	}
	else {
	    int i=getHead();
	    Vector vFifo=_vFifo;
	    int iCount=getSize()+getHead();
	    while (i<iCount && vFifo.get(i).getClass()==clsSignal)
		i++;
	    _iHead=i;
	}
    }
}
