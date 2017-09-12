package eu.telecomlille.sdl;

/**
 * Helper to test threaded classes.
 * 
 * @see StepControlled
 * @see StepController
 * @author Christophe TOMBELLE
 */
public class Stepper implements StepController, StepControlled {
	protected int count;
	protected String name;
	public Stepper() {
		this.name = "";
	}
	public Stepper(String name) {
		this.name = name;
	}
	protected void await() {
		try {
			wait();
		} catch (InterruptedException e) {
		}
	}

	public void begin() {
		synchronized (this) {
			count++;
			if (count == 1)
				await();
		}
	}

	public void block() {
		synchronized (this) {
			count++;
			if (count == 2)
				notify();
			await();
		}
	}

	public void step() {
		synchronized (this) {
			notify();
			count = 1;
			await();
		}
	}

	@Override
	public void end() {
		synchronized (this) {
			notify();
		}
	}
	@Override
	public String toString() {
		return name+"("+count+")";
	}

}
