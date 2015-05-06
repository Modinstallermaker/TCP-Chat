package pck;

import general.MessageEvent;

public class TextMessageEvent extends MessageEvent {
	private final String text;

	public TextMessageEvent(int senderID, int receiverID, String text) {
		super(senderID, receiverID);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public boolean isBroadCastMessage() {
		return getReceiverID() == ChatProtocoll.ID_ALL;
	}

}
