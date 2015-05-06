package pck.server;

import static pck.ChatProtocoll.*;
import general.CommChannel;
import general.MessageEvent;
import general.MultiClientServer;
import general.Receiver;

import java.util.List;

import pck.ChatProtocoll;
import pck.TextMessageEvent;

public class ChatServerGUI implements Receiver {

	// Messages have following structure:
	// Client ---> Server:
	// "receiverID:::senderID:::command:::content"
	// Server ---> Client:
	// "sender:::command:::content"

	private final List<CommChannel> clientList;

	public ChatServerGUI() {
		this.clientList = new MultiClientServer("ChatServer", this).getClientList();
	}

	@Override
	public void receiveNextDataPack(MessageEvent msg, CommChannel source) {
		
		final int senderID = source.getId();
		final int receiverID = msg.getReceiverID(), sender = msg.getSenderID();
		
		if (msg instanceof TextMessageEvent)) {			
			broadcastAll(msg);
		}
		else if (command.equals(CMD_EXIT)) {
			broadcastAll(msg);
		}
		else if (command.equals(CMD_TELL_JOINT_MEMBERS)) {
			broadcastAll(msg);
		}
		else if (command.equals(CMD_TELL_JOINT_MEMBERSLIST)) {
			String members ="";
			for(CommChannel client : clientList){
			   members+=String.valueOf(client.getId())+SEPARATE_1; 			    
			}
			if(members.length()>2)
				members = members.substring(0, members.length()-SEPARATE_1.length());
			
			broadcastAll(ID_ALL + SEPARATE_0 + senderID + SEPARATE_0
					+ CMD_TELL_JOINT_MEMBERSLIST + SEPARATE_0 + members);
		}
		else if (command.equals(CMD_SET_NAME)) {
			broadcastAll(ID_ALL + SEPARATE_0 + senderID + SEPARATE_0
					+ CMD_SET_NAME + SEPARATE_0 + senderID + SEPARATE_1
					+ content);
		} 
		else if (command.equals(CMD_SEND_PRIVATE_MSG)) {
			throw new UnsupportedOperationException("not implemented yet");
		} 
		else {
			throw new IllegalArgumentException(msg);
		}
	}

	private void broadcastAll(String msg) {
		for (CommChannel client : clientList) {
			client.transmit(msg);
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
		source.transmit(ChatProtocoll.tellIDDataPack(id));
		broadcastAll(ChatProtocoll.clientJoinedDataPack(id));
	}

	@Override
	public void disconnected(CommChannel source) {
		final int id = source.getId();
		broadcastAll(ChatProtocoll.clientExitedDataPack(id));
	}
}
