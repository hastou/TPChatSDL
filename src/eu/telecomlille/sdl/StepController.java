package eu.telecomlille.sdl;

/**
 * Stepper interface for the controller thread.
 * 
 * @see Stepper
 * @author Christophe TOMBELLE
 */
public interface StepController {
	/**
	 * Wait the controlled thread has reached its first block() call.
	 */
	void begin();

	/**
	 * Release the controlled thread from its current block() and wait it has
	 * reached the next block() call.
	 */
	void step();

	/**
	 * Release the controlled from its current (and supposedly last) block()
	 * call.
	 */
	void end();
}
