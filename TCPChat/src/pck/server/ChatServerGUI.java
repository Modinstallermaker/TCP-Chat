package pck.server;

import static pck.ChatProtocoll.ID_ALL;
import general.CommChannel;
import general.MessageEvent;
import general.MultiClientServer;
import general.Receiver;

import java.util.List;

import pck.ExitEvent;
import pck.ReNameEvent;
import pck.TellIDEvent;
import pck.TextMessageEvent;

public class ChatServerGUI implements Receiver {

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
			broadcast(e);
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
