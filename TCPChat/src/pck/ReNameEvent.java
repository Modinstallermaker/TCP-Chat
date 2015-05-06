package pck;

import general.MessageEvent;

public class ReNameEvent extends MessageEvent {
	private final String name;

	public ReNameEvent(int senderID, int receiverID, String name) {
		super(senderID, receiverID);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isBroadCastMessage() {
		return true;
	}

}
