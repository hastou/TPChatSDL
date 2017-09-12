package eu.telecomlille.sdl;

import java.util.HashMap;

public class Registry implements IRegistry {
	public static IRegistry INSTANCE = new Registry();
	protected HashMap<String, IProcess> _hmClients;

	public Registry() {
		_hmClients = new HashMap<String, IProcess>();
		_hmClients.put("null", null);
	}

	/**
	 * Register a process instance using its pid as a key for future associative
	 * access.
	 * 
	 * @param The
	 *            pid of the process instance being registered.
	 * @param The
	 *            process instance being registered.
	 * @see IRegistry#registerProc(String, IProcess)
	 */
	@Override
	public void registerProc(String strPId, IProcess pid) {
		// TODO : penser aux accès concurrents
		synchronized (_hmClients) {
			_hmClients.put(strPId, pid);
		}
	}

	/**
	 * Get a previously registered process instance from its pid.
	 * 
	 * @param The
	 *            pid of the required process instance.
	 * @return The required process instance.
	 * @see IRegistry#getProc(String)
	 */
	@Override
	public IProcess getProc(String strPId) {
		// TODO : penser aux accès concurrents
		synchronized (_hmClients) {
			return _hmClients.get(strPId);
		}
	}

	/**
	 * Unregister a previously registered process instance.
	 * 
	 * @param The
	 *            pid of the process instance to unregister.
	 * @see IRegistry#unregisterProc(String)
	 */
	@Override
	public void unregisterProc(String strPId) {
		// TODO : penser aux accès concurrents
		synchronized (_hmClients) {
			_hmClients.remove(strPId);
		}
	}
}
