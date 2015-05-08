package pck;

import general.MessageEvent;

public class TextMessageEvent extends MessageEvent {

	private static final long serialVersionUID = 2797709311179134523L;
	private final String text;

	public TextMessageEvent(int senderID, int receiverID, String text) {
		super(senderID, receiverID);
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	@Override
	public boolean isBroadCastMessage() {
		return getReceiverID() == ChatProtocoll.ID_ALL;
	}

}
