package eu.telecomlille.sdl;

/**
 * This is the event class to support the eu.tl1.sdl.SignalListener interface.
 */
public abstract class Signal implements ISignal {
	protected IProcess sender;
	/**
	 * SignalEvent constructor comment.
	 * 
	 * @param sender
	 *            java.lang.Object
	 */
	public Signal(IProcess sender) {
		this.sender = sender;
	}
	public IProcess getSender() {
		return sender;
	}
	public String toString() {
		return getClass().getSimpleName()+"("+sender+")";
	}
}
