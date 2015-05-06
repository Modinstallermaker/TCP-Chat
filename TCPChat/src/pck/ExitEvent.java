package pck;

import general.MessageEvent;
import static pck.ChatProtocoll.*;
public class ExitEvent extends MessageEvent {
	private final int exiterID;
	public ExitEvent(int receiverID, int exiterID) {
		super(ID_SERVER, ID_ALL);
		this.exiterID = exiterID;
	}
	@Override
	public boolean isBroadCastMessage() {
		// TODO Auto-generated method stub
		return true;
	}
	public int getExiterID() {
		return exiterID;
	}

}
