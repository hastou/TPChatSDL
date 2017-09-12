package eu.telecomlille.sdl;

/**
 * A simple SDL Process interface.
 * 
 * @author: C. TOMBELLE
 */
public interface IProcess extends ISignalListener {

	/**
	 * Set the parent process id.
	 * 
	 * @param procParent
	 *            The parent process.
	 * @param s
	 *            The Stepper to use in debug mode only
	 */
	void setParent(IProcess procParent, Stepper s);
}
