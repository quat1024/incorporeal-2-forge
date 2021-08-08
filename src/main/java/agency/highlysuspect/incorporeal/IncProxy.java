package agency.highlysuspect.incorporeal;

public interface IncProxy {
	default void setup() {}
	
	class Server implements IncProxy {}
}
