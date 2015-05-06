package pck.client;

import static pck.ChatProtocoll.*;
import general.ClientPlugin;
import general.CommChannel;
import general.Receiver;
import general.MessageEvent;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import pck.ExitEvent;
import pck.ReNameEvent;
import pck.TellIDEvent;
import pck.TextMessageEvent;

public class ChatClientGUI extends JFrame implements ActionListener,
		KeyListener, Receiver, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -978956615372167638L;
	private CommChannel server;
	private int clientID;

	// Components
	private final ClientPlugin clientPP;
	private final JButton btnClear = new JButton(
			"<html><body>Verlauf<br>l\u00F6schen</body></html>");
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private final JList<String> listMembers = new JList<String>(model);
	private final JEditorPane txtOutput = new JEditorPane("text/html", null);
	private final JTextArea txtInput = new JTextArea();
	private final JButton btnSend = new JButton("Senden");
	private final HTMLEditorKit kit = new HTMLEditorKit();
	private final HTMLDocument doc = new HTMLDocument();
	private String Outputtext = "<td colspan=3><b>Herzlich Willommen im Chat!</b><br></td>";
	boolean scrollDown = false;
	private final List<ReNameEvent> clientNames = new ArrayList<>();

	public ChatClientGUI() {
		super("Nicht verbunden");
		clientPP = new ClientPlugin(this, this);
		buildGUI();
	}

	protected void buildGUI() {
		JSplitPane contentLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				clientPP, new JScrollPane(listMembers));

		txtOutput.setEditable(false);
		txtOutput.setDocument(doc);
		txtOutput.setEditorKit(kit);

		JScrollPane scrollPaneTextOut = new JScrollPane(txtOutput);
		scrollPaneTextOut.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						if (scrollDown)
							e.getAdjustable().setValue(
									e.getAdjustable().getMaximum());
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

		JSplitPane contentRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				true, scrollPaneTextOut, contBottom);
		contentRight.setResizeWeight(0.95);
		contentRight.setDividerLocation(0.5);

		JSplitPane splitPaneContent = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, contentLeft, contentRight);

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
		txtOutput.setText("<html><body><table width='100%'>" + Outputtext
				+ "</table></body></html>");
		scrollDown = true;
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
			append("<td colspan=3><i>Konsole wurde gel\u00fcscht...</i></td>");
		}
	}

	public void senden() {
		String neu = txtInput.getText().trim();
		if (neu.length() > 0) {
			sendBroadcastTextMessage(neu);
			txtInput.setText("");
		}
	}

	@Override
	public void receiveNextMessage(MessageEvent e, CommChannel source) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"HH:mm:ss");
		String date = sdf.format(new Date());
		System.out.println(e.getClass().getName());
		 int senderID = e.getSenderID();
		 int receiverID = e.getReceiverID();
		 String sender = clientNameOf(e.getSenderID());
		 
		if (e instanceof TextMessageEvent) {
			final TextMessageEvent txtME = (TextMessageEvent) e;
			System.out.println("txtEv ist angekommen");

			String content = txtME.getText();
			append("<td valign='top' width='15%'><b>" + sender
					+ ": </b></td><td width='85%'>" + content + "</td><td><i>"
					+ date + "</i></td>");

		} else if (e instanceof ReNameEvent) {
			final ReNameEvent rnE = (ReNameEvent) e;
			System.out.println("Rename ist angekommen");
			if (! this.clientNames.contains(e)) { // new join of somebody
				model.addElement(rnE.getName());
				this.clientNames.add((ReNameEvent) e);
				append("<td valign='top' width='15%'><b>" + sender
						+ ": </b></td><td width='85%'>" 
						+ " ist dem Chat beigetreten" + "</td><td><i>" + date
						+ "</i></td>");
			}else { // name change
				// not necessary
			}
			
		} else if (e instanceof ExitEvent) {
			final ExitEvent exE = (ExitEvent) e;
//			String exiter = clientNames.exE.getExiterID();// clientName with id 
//			model.removeElement(exiter);
//			append("<td valign='top' width='15%'><b>" + sender
//					+ ": </b></td><td width='85%'>" + exiter
//					+ " hat den Chat verlassen" + "</td><td><i>" + date
//					+ "</i></td>");
		} 
		else   if (e instanceof TellIDEvent) {
			this.clientID = e.getReceiverID(); // my own id
			setTitle("Client " + this.clientID);
			append("<td width='15%'><b>" + sender
					+ ": </b></td><td width='85%'>"
					+ "Deine Client-ID lautet: " + clientID + "</td><td><i>"
					+ date + "</i></td>");
		} 
	}

	private String clientNameOf(int senderID) {
		// TODO Auto-generated method stub
		return null;
		}	
		

	@Override
	public void connected(CommChannel source) {
		this.server = source;
		transmit(new ReNameEvent(clientID, ID_ALL, "Hans Dampf"));
		this.setTitle("Verbunden");
		String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
		append("<td valign='top' colspan=2><b>Sie wurden mit dem Server verbunden</b></td><td><i>"
				+ date + "</i></td>");
		this.txtInput.setEditable(true);
		this.btnSend.setEnabled(true);
		this.btnSend.setEnabled(true);			
//		transmit(new ReNameEvent(-55242542, ID_ALL, "Hans Peter"));
	}

	@Override

	public void disconnected(CommChannel source, boolean causedByOtherEnd) {
		if (causedByOtherEnd) {
			append("<td colspan=3><i><b>Die Serververbindung wurde unterbrochen...</b></i></td>");
		} else {		
			append("<td colspan=3><i><b>Sie haben sich vom Chat abgemeldet...</b></i></td>");
		}	
		append("<td colspan=3><b>Vielen Dank f\u00fcr die Nutzung des Chat Programms!</b><br><br>"
				+ "<i>Sie sind nun NICHT mehr beim Server angemeldet!<br>"
				+ "Zum erneuten Verbindunsgsaufbau bitte oben links auf \"Verbinden\" klicken...</i></td>");
		
		server = null; // let gc do its job
		model.clear();
		txtInput.setEditable(false);
		btnSend.setEnabled(false);
	}

	private void transmit(MessageEvent dataPacket) {
		server.transmit(dataPacket);
	}

	private void sendBroadcastTextMessage(String text) {
		transmit(new TextMessageEvent(clientID, ID_ALL, text));
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
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		scrollDown = false;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
