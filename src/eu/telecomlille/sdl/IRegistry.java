package eu.telecomlille.sdl;


public interface IRegistry {
	void registerProc(String pid, IProcess proc);
	IProcess getProc(String pid);
	void unregisterProc(String pid);
}
