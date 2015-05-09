package pck;

import general.MessageEvent;

public class ReNameEvent extends MessageEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3021190636770356284L;
	private final String name;

	public ReNameEvent(int senderID, int receiverID, String name) {
		super(senderID, receiverID);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean isBroadCastMessage() {
		return true;
	}

}
