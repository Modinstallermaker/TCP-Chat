package pck.server;

import static pck.ChatProtocoll.*;
import general.CommChannel;
import general.MessageEvent;
import general.MultiClientServer;
import general.Receiver;

import java.util.List;

import pck.ChatProtocoll;
import pck.ExitEvent;
import pck.ReNameEvent;
import pck.TellIDEvent;
import pck.TextMessageEvent;

public class ChatServerGUI implements Receiver {

	// Messages have following structure:
	// Client ---> Server:
	// "receiverID:::senderID:::command:::content"
	// Server ---> Client:
	// "sender:::command:::content"

	private final List<CommChannel> clientList;

	public ChatServerGUI() {
		this.clientList = new MultiClientServer("ChatServer", this)
				.getClientList();
	}

	@Override
	public void receiveNextMessage(MessageEvent e, CommChannel source) {
		if (e instanceof TextMessageEvent) {
			final TextMessageEvent txtME = (TextMessageEvent) e;
			if (txtME.isBroadCastMessage()) {
				broadcast(e);
			} else {
				throw new UnsupportedOperationException();
			}
		} else if (e instanceof ExitEvent) {
			throw new UnsupportedOperationException("Not impled yet");
		}

		else if (e instanceof ReNameEvent) {
			broadcast(e);
			
		}  else {
			throw new IllegalArgumentException(e.toString());
		}
	}

	private  void broadcast(MessageEvent msg) {
		synchronized (this.clientList) {
			for (CommChannel client : this.clientList) {
				client.transmit(msg);
			}
		}
	}

	// private void broadcastExcept(String msg, String id) {
	//
	// for (Server client : clientList) {
	// if(Integer.parseInt(id)!=client.getId())
	// client.transmit(msg);
	// }
	// }

	@Override
	public void connected(CommChannel source) {
		final int id = source.getId();
		source.transmit(new TellIDEvent(id));
		
	}

	@Override
	public void disconnected(CommChannel source, boolean causedByClient) {
		final int exiterID = source.getId();
		broadcast(new ExitEvent(ID_ALL, exiterID, causedByClient));
	}
}
