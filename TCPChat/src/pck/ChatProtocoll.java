package pck;

public class ChatProtocoll {
	public final static String SEPARATE_0 = "::::";
	public final static String SEPARATE_1 = ":::";
	public final static String SEPARATE_2 = "::";
	public final static String SEPARATE_3 = ":";
	public final static String CMD_TELL_ID = "tellId";
	public final static String CMD_SET_NAME = "setName";
	public final static String CMD_BROAD_CAST = "sendMsg";
	public final static String CMD_SEND_PRIVATE_MSG = "sendPrivateMsg";
	public final static String CMD_DISCONNECT = "disconnect";
	public final static String CMD_EXIT = "exit";
	public final static String CMD_JOIN = "JOIN";
	public final static String CMD_TELL_JOINT_MEMBERS = "tellJointMembers";
	public final static String CMD_TELL_JOINT_MEMBERSLIST = "tellJointMembersList";
	public final static int ID_SERVER = -1;
	public final static int ID_ALL = -2;
	public final static int INDEX_RECEIVER_ID = 0;
	public final static int INDEX_SENDER_ID = 1;
	public final static int INDEX_CMD = 2;
	public final static int INDEX_CONTENT = 3;
	/**
	 * ID for receiving client who hasn't got an id yet
	 */
	private static final String ID_YOU = "YOU";
	private static final String NAME_SERVER = "Server";

	public static String broadCastDataPack(int senderID, String msgText) {
		// "receiverID::::senderID::::command::::content"
		String dp = ID_ALL + SEPARATE_0 + senderID + SEPARATE_0
				+ CMD_BROAD_CAST + SEPARATE_0 + msgText;
		return dp;
	}

	public static String setNameDataPack(String name) {
		throw new UnsupportedOperationException("not impled yet");
	}

	public static String tellIDDataPack(int id) {
		String dp = ID_YOU + SEPARATE_0 + ID_SERVER + SEPARATE_0 + CMD_TELL_ID
				+ SEPARATE_0 + id;
		return dp;
	}

	public static String privateMessageDataPack(int receiverID, String msgText) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public static String clientJoinedDataPack(int clientID) {
		// "receiverID::::senderID::::command::::content"
		String dp = ID_ALL + SEPARATE_0 + ID_SERVER + SEPARATE_0 + CMD_TELL_JOINT_MEMBERS
				+ SEPARATE_0 + clientID;
		return dp;
	}

	public static String clientExitedDataPack(int clientID) {
		// "receiverID::::senderID::::command::::content"
		String dp = ID_ALL + SEPARATE_0 + ID_SERVER + SEPARATE_0 + CMD_EXIT
				+ SEPARATE_0 + clientID;
		return dp;
	}
	public static String tellJointMembersDataPack() {
		// "receiverID::::senderID::::command::::stringchatmitglieder"	
		String dp = ID_ALL + SEPARATE_0 + ID_SERVER + SEPARATE_0 + CMD_TELL_JOINT_MEMBERSLIST + SEPARATE_0 +"xxx";		
		return dp;
	}

	private ChatProtocoll() {
	}

	public static String[] split(String msg) {
		return msg.split(SEPARATE_0);
	}

	public static String nameOf(int senderID) {
		return (senderID == ID_SERVER) ? NAME_SERVER : "Client " + senderID;
	}
}
