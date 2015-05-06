package pck;

import general.MessageEvent;
import static pck.ChatProtocoll.*;
public class ExitEvent extends MessageEvent {
	private final int exiterID;
	private final boolean causedByExiter;
	public ExitEvent(int receiverID, int exiterID, boolean causedByExiter) {
		super(ID_SERVER, ID_ALL);
		this.exiterID = exiterID;
		this.causedByExiter = causedByExiter;
	}
	@Override
	public boolean isBroadCastMessage() {
		// TODO Auto-generated method stub
		return true;
	}
	public int getExiterID() {
		return exiterID;
	}
	public boolean isCausedByExiter() {
		return causedByExiter;
	}

}
