package general;

import java.io.Serializable;

public abstract class MessageEvent implements Serializable {
	/**
	 * The network global ID of sender of this data pack which is set by MultiClientServer
	 */
	private final int senderID;
	/**
	 * The network global ID of receiver this data pack which is set by MultiClientServer
	 */
	private final int receiverID;
	
	
	protected MessageEvent(int senderID, int receiverID) {
		this.senderID = senderID;
		this.receiverID = receiverID;

	}


	public int getSenderID() {
		return this.senderID;
	}


	public int getReceiverID() {
		return this.receiverID;
	}


	public abstract boolean isBroadCastMessage();

}
