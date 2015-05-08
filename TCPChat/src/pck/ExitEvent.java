package pck;

import general.MessageEvent;
import static pck.ChatProtocoll.*;
public class ExitEvent extends MessageEvent {
	private static final long serialVersionUID = 6939257521743529286L;
	private final int exiterID;
	private final boolean causedByExiter;
	public ExitEvent(int receiverID, int exiterID, boolean causedByExiter) {
		super(ID_SERVER, ID_ALL);
		this.exiterID = exiterID;
		this.causedByExiter = causedByExiter;
	}
	@Override
	public boolean isBroadCastMessage() {
		return true;
	}
	public int getExiterID() {
		return this.exiterID;
	}
	public boolean isCausedByExiter() {
		return this.causedByExiter;
	}

}
