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
}
