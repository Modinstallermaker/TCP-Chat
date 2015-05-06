package general;

public interface Receiver {

	void receiveNextDataPack(String msg, Connection source);

	void connected(Connection source);

	/**
	 * this method is called when connection has been interrupted from other end
	 * or caused by own interruption. if receiver in own jvm (on client or
	 * server side) called Channel.disconnect(), second param will be false
	 * @param source, the object (Channel) which calls this method
	 */
	void disconnected(Connection source);
}
