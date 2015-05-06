package pck;

import general.MessageEvent;
import static pck.ChatProtocoll.*;
public class TellIDEvent extends MessageEvent {
	public TellIDEvent(int newReceiverID) {
		super(ID_SERVER, newReceiverID);
	}



	@Override
	public final boolean isBroadCastMessage() {
		return false;
	}

}
