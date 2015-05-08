package pck;

import general.MessageEvent;
import static pck.ChatProtocoll.*;
public class TellIDEvent extends MessageEvent {

	private static final long serialVersionUID = 5924440366693887124L;



	public TellIDEvent(int newReceiverID) {
		super(ID_SERVER, newReceiverID);
	}



	@Override
	public final boolean isBroadCastMessage() {
		return false;
	}



	public String getServerName() {
		// TODO Auto-generated method stub
		return "Server";
	}

}
