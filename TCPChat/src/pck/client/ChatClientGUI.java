package pck.client;

import static pck.ChatProtocoll.*;
import general.ClientPlugin;
import general.CommChannel;
import general.Receiver;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import pck.ChatProtocoll;

public class ChatClientGUI extends JFrame implements ActionListener, KeyListener, Receiver, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -978956615372167638L;
	private CommChannel server;
	private int clientID;

	// Components
	private final ClientPlugin clientPP;
	private final JButton btnClear = new JButton("<html><body>Verlauf<br>löschen</body></html>");
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private final JList<String> listMembers = new JList<String>(model);
	private final JEditorPane txtOutput = new JEditorPane("text/html", null);
	private final JTextArea txtInput = new JTextArea();
	private final JButton btnSend = new JButton("Senden");
	private final HTMLEditorKit kit = new HTMLEditorKit();
	private final HTMLDocument doc = new HTMLDocument();
	private String Outputtext = "<td colspan=3><b>Herzlich Willommen im Chat!</b><br></td>";
	boolean scrollDown = false;

	public ChatClientGUI() {
		super("Nicht verbunden");
		clientPP = new ClientPlugin(this, this);
		buildGUI();
	}

	protected void buildGUI() {
		JSplitPane contentLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT,	clientPP, new JScrollPane(listMembers));
		
			txtOutput.setEditable(false);
			txtOutput.setDocument(doc);
			txtOutput.setEditorKit(kit);
			
			JScrollPane scrollPaneTextOut = new JScrollPane(txtOutput);
			scrollPaneTextOut.getVerticalScrollBar().addAdjustmentListener(
					new AdjustmentListener() {
						public void adjustmentValueChanged(AdjustmentEvent e) {
							if(scrollDown)
								e.getAdjustable().setValue(e.getAdjustable().getMaximum());	
						}
					});
			scrollPaneTextOut.addMouseListener(this);
			
				Container contButtons = new JPanel();
				contButtons.setLayout(new BorderLayout());
				contButtons.add(btnSend, BorderLayout.CENTER);
				contButtons.add(btnClear, BorderLayout.SOUTH);
			
			Container contBottom = new JPanel();
			contBottom.setLayout(new BorderLayout());
			contBottom.add(new JScrollPane(txtInput), BorderLayout.CENTER);
			contBottom.add(contButtons, BorderLayout.EAST);
		
		JSplitPane contentRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT,	true, scrollPaneTextOut, contBottom);
		contentRight.setResizeWeight(0.95);
		contentRight.setDividerLocation(0.5);
		
		JSplitPane splitPaneContent = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, contentLeft, contentRight);	
		
		add(splitPaneContent);
		txtInput.addKeyListener(this);
		btnSend.addActionListener(this);	
		btnClear.addActionListener(this);	
		
		append("<td colspan=3><i>Bitte klicken Sie links oben auf \"Verbinden\"...</i></td>");
		
		setSize(700, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		pack();
		setLocationRelativeTo(null);
		txtInput.setEditable(false);
		btnSend.setEnabled(false);
	}

	public void append(String s) {
		Outputtext += "<tr>" + s + "</tr>";
		txtOutput.setText("<html><body><table width='100%'>" + Outputtext + "</table></body></html>");
		scrollDown=true;	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == btnSend) {
			senden();
		} else if (src == btnClear) {
			Outputtext = " ";
			append("<td colspan=3><i>Konsole wurde gelöscht...</i></td>");
		}
	}

	public void senden() {		
		String neu = txtInput.getText().trim();
		if (neu.length() > 0) {
			sendBroadcastMessage(neu);
			txtInput.setText("");
		}
	}

	@Override
	public void receiveNextDataPack(String msg, CommChannel source) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");

		String[] parts = ChatProtocoll.split(msg);

		int senderID = Integer.parseInt(parts[INDEX_SENDER_ID]);
		String command = parts[INDEX_CMD];
		String receiverID = parts[INDEX_RECEIVER_ID];
		String content = parts[INDEX_CONTENT];
		String date = sdf.format(new Date());
		String sender = ChatProtocoll.nameOf(senderID);

		if (command.equals(CMD_BROAD_CAST)) {			
			append("<td valign='top' width='15%'><b>" + sender
					+ ": </b></td><td width='85%'>" + content + "</td><td><i>"
					+ date + "</i></td>");
		} else if (command.equals(CMD_TELL_JOINT_MEMBERS)) {			
			String joiner = nameOf(Integer.parseInt(content));
			model.addElement(joiner);
			append("<td valign='top' width='15%'><b>" + sender
					+ ": </b></td><td width='85%'>" + joiner
					+ " ist dem Chat beigetreten" + "</td><td><i>" + date
					+ "</i></td>");
		} else if (command.equals(CMD_EXIT)) {
			String exiter = nameOf(Integer.parseInt(content));
			model.removeElement(exiter);
			append("<td valign='top' width='15%'><b>" + sender
					+ ": </b></td><td width='85%'>" + exiter
					+ " hat den Chat verlassen" + "</td><td><i>" + date
					+ "</i></td>");
		} else if (command.equals(CMD_TELL_ID)) {
			this.clientID = Integer.parseInt(content);
			setTitle("Client " + this.clientID);
			append("<td width='15%'><b>" + sender
					+ ": </b></td><td width='85%'>"
					+ "Deine Client-ID lautet: " + content + "</td><td><i>"
					+ date + "</i></td>");
		}	
		else if (command.equals(CMD_TELL_JOINT_MEMBERSLIST)) {			
			String[] clientList = content.split(SEPARATE_1);
			model.removeAllElements();
			for (String cl : clientList)			
				model.addElement("Client "+cl);
		}
	}

	@Override
	public void connected(CommChannel source) {
		this.server = source;		
		this.setTitle("Verbunden");
		String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
		append("<td valign='top' colspan=2><b>Sie wurden mit dem Server verbunden</b></td><td><i>"+ date + "</i></td>");
		this.txtInput.setEditable(true);
		this.btnSend.setEnabled(true);			
		transmit(tellJointMembersDataPack());
	}
	
	@Override
	public void disconnected(CommChannel source) {
		if(clientPP.causedbyme)
			append("<td colspan=3><i><b>Sie haben sich vom Chat abgemeldet...</b></i></td>");
		else
			append("<td colspan=3><i><b>Die Serververbindung wurde beendet...</b></i></td>");
		append("<td colspan=3><b>Vielen Dank für die Nutzung des Chat Programms!</b><br><br>"
				+ "<i>Sie sind nun NICHT mehr beim Server angemeldet!<br>"
				+ "Zum erneuten Verbindunsgsaufbau bitte oben links auf \"Verbinden\" klicken...</i></td>");
		
		server = null; // let gc do its job
		model.clear();
		txtInput.setEditable(false);
		btnSend.setEnabled(false);
	}

	private void transmit(String dataPacket) {
		server.transmit(dataPacket);
	}

	private void sendBroadcastMessage(String text) {
		transmit(broadCastDataPack(clientID, text.replace("\n", "<br>")));
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Object src = e.getSource();
		int key = e.getKeyCode();
		if (src == txtInput) {
			if (e.isShiftDown() && key == KeyEvent.VK_ENTER) {
				txtInput.append(System.getProperty("line.separator"));
			} else if (key == KeyEvent.VK_ENTER) {
				senden();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		scrollDown=false;		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}	
}
