package agency.highlysuspect.rhododendrite;

public interface RhoProxy {
	default void setup() {}
	
	class Server implements RhoProxy {}
}
