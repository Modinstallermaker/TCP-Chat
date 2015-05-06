package general;

public interface Receiver {

	void receiveNextMessage(MessageEvent msg, CommChannel source);

	void connected(CommChannel source);

	/**
	 * this method is called when connection has been interrupted from other end
	 * or caused by own interruption. if receiver in own jvm (on client or
	 * server side) called Channel.disconnect(), second param will be false
	 * @param source, the object (Channel) which calls this method
	 */
	void disconnected(CommChannel source, boolean causedByOtherEnd);

	void receiveNextDataPack(MessageEvent msg, CommChannel source);
}
