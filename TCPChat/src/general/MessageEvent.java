package general;

import java.io.Serializable;

public abstract class MessageEvent implements Serializable {
	private static final long serialVersionUID = -4372367554149485322L;
	/**
	 * The network global ID of sender of this data pack which is set by MultiClientServer
	 */
	private int senderID;
	/**
	 * The network global ID of receiver this data pack which is set by MultiClientServer
	 */
	private int receiverID;
	
	
	protected MessageEvent(int senderID, int receiverID) {
		this.senderID = senderID;
		this.receiverID = receiverID;

	}


	public int getSenderID() {
		return this.senderID;
	}
	
	public void setSenderID(int id) {
		this.senderID =id;
	}	


	public int getReceiverID() {
		return this.receiverID;
	}


	public abstract boolean isBroadCastMessage();

}
