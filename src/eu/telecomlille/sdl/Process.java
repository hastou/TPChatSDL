package eu.telecomlille.sdl;

/**
 * Classe de base pour un process SDL ; une instanciation de cette classe Java
 * suivie d'un appel à {@link #setParent(IProcess, Stepper)} implante une
 * instanciation de process SDL.
 * 
 * @author Christophe TOMBELLE
 */
public class Process implements IProcess {
	// Expressions prédéfinies de type PId.
	protected IProcess offspring;
	protected IProcess parent;
	protected IProcess self;
	protected IProcess sender;
	/** Fifo SDL. */
	protected Fifo _fifo;
	/** signal prélevé en tête de fifo. */
	protected ISignal _sig;
	/** Contrôle la terminaison de cette instance de process. */
	protected volatile boolean _bRun;
	/** Contrôle le démarrage de cette instance de process. */
	protected Thread thread;
	/** Stepper (pour debug) du thread de cette instance de process. */
	protected Stepper stepper;
	/** Stepper (pour debug) du dernier fils créé. */ 
	protected Stepper stpOffspring;

	/**
	 * Créer une instance de ce process (à compléter par un appel à {@link #setParent(IProcess, Stepper)}).
	 */
	public Process() {
		self = this;
		_fifo = new Fifo();
	}

	/**
	 * Délègue à {@link Stepper#block()} du Stepper du thread de ce Process.
	 */
	protected void block() {
		if (stepper != null)
			stepper.block();
	}

	/**
	 * Ajouter le signal spécifié à la fifo de l'instance de process.
	 */
	@Override
	public void add(ISignal sig) {
		synchronized (_fifo) {
			_fifo.add(sig);
			if (_fifo.getSize() == 1)
				_fifo.notify();
		}
	}

	/**
	 * Fournir le {@link Stepper} du dernier fils créé (par un CREATE) ou null
	 * si aucun fils créé.
	 * 
	 * @return Le Stepper du fils.
	 */
	public Stepper getOffspringStepper() {
		return stpOffspring;
	}

	/**
	 * Support pour CREATE établissant les relations père / fils et démarrant le
	 * thread du fils; à utiliser dans le thread du père (contrôlé par
	 * stpCaller).
	 * 
	 * @param stpCaller
	 *            Le stepper pour contrôler le thread du process père ou null si
	 *            pas de debug souhaité.
	 * @param pidOffspring
	 *            Le process fils.
	 */
	public void create(Stepper stpCaller, IProcess pidOffspring) {
		offspring = pidOffspring;
		if (stpCaller != null) {
			stpOffspring = new Stepper();
			/*
			 * Permettre au test d'acquérir stpOffspring avant que le fils
			 * démarre.
			 */
			stpCaller.block();
		}
		offspring.setParent(this, stpOffspring);
	}

	/**
	 * Support pour CREATE établissant les relations père / fils et démarrant le
	 * thread du fils.
	 * 
	 * @param pidOffspring
	 *            Le process fils.
	 */
	public void create(IProcess pidOffspring) {
		create(stepper, pidOffspring);
	}

	/**
	 * Définir le parent de ce Process et démarrer cette instance de process.
	 */
	@Override
	public void setParent(IProcess procParent, Stepper s) {
		parent = procParent;
		stepper = s;
		thread = new Thread(new Runnable() {
			public void run() {
				_bRun = true;
				onStart();
				block();
				while (_bRun)
					try {
						_sig = null;
						synchronized (_fifo) {
							while (_fifo.isEmpty()) {
//								System.out.println("blocking on fifo");
								_fifo.wait();
							}
							_sig = (Signal) _fifo.get();
						}
						sender = _sig.getSender();
						// dispatch following current state
						dispatch();
						block();
					} catch (InterruptedException e) {
					}
			}
		});
//		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Support pour le STOP SDL ; appeler cette méthode pour terminer cette
	 * instance de process.
	 */
	protected void stop() {
		_bRun = false;
	}

	/**
	 * Cette méthode est appelée quand le thread de ce process démarre, avant
	 * que la présence d'un signal soit recherché en tête de fifo ; à redéfinir
	 * dans une sous-classe pour implanter la pseudo-transition d'initialisation
	 * (support pour START).
	 */
	protected void onStart() {
	}

	/**
	 * Cette méthode est appelée quand un signal disponible dans _sig a été
	 * prélevé en tête de Fifo ; à redéfinir dans une sous-classe pour implanter
	 * la réaction au signal _sig selon l'état courant.
	 */
	protected void dispatch() {
	}

	/**
	 * Indiquer si cette instance de process est terminée.
	 * 
	 * @return true si instance de process terminée.
	 */
	public boolean isStopped() {
		return !_bRun;
	}
}
