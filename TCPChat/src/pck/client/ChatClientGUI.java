package pck.client;

import static pck.ChatProtocoll.*;
import general.ClientPlugin;
import general.CommChannel;
import general.Receiver;
import general.MessageEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
	private DefaultListModel<String> model = new DefaultListModel<>();
	private final JList<String> listMembers = new JList<>(this.model);
	private final JEditorPane txtOutput = new JEditorPane("text/html", null);
	private final JTextArea txtInput = new JTextArea();
	private final JLabel lblName = new JLabel("Name: ");
	private final JTextField txtName = new JTextField(System.getProperty("user.name"));
	private final JButton btnSend = new JButton("Senden");
	private final HTMLEditorKit kit = new HTMLEditorKit();
	private final HTMLDocument doc = new HTMLDocument();
	private String Outputtext = "<td colspan=3><b>Herzlich Willommen im Chat!</b><br></td>";
	boolean scrollDown = false;
	private final List<ReNameEvent> clientNames = new ArrayList<>();

	public ChatClientGUI() {
		super("Nicht verbunden");
		this.clientPP = new ClientPlugin(this, this);
		buildGUI();
	}

	protected void buildGUI() {
		
		Container memberPane = new Container();
		memberPane.setLayout(new BorderLayout());
		memberPane.add(this.txtName, BorderLayout.CENTER);
		memberPane.add(this.lblName, BorderLayout.WEST);
		this.txtName.setPreferredSize(this.txtName.getSize());

		memberPane.add(new JScrollPane(this.listMembers), BorderLayout.SOUTH);
		JSplitPane contentLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				this.clientPP, memberPane);

		this.txtOutput.setEditable(false);
		this.txtOutput.setDocument(this.doc);
		this.txtOutput.setEditorKit(this.kit);

		JScrollPane scrollPaneTextOut = new JScrollPane(this.txtOutput);
		scrollPaneTextOut.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						if (ChatClientGUI.this.scrollDown)
							e.getAdjustable().setValue(
									e.getAdjustable().getMaximum());
					}
				});
		scrollPaneTextOut.addMouseListener(this);

		Container contButtons = new JPanel();
		contButtons.setLayout(new BorderLayout());
		contButtons.add(this.btnSend, BorderLayout.CENTER);
		contButtons.add(this.btnClear, BorderLayout.SOUTH);

		Container contBottom = new JPanel();
		contBottom.setLayout(new BorderLayout());
		contBottom.add(new JScrollPane(this.txtInput), BorderLayout.CENTER);
		contBottom.add(contButtons, BorderLayout.EAST);

		JSplitPane contentRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				true, scrollPaneTextOut, contBottom);
		contentRight.setResizeWeight(0.95);
		contentRight.setDividerLocation(0.5);

		JSplitPane splitPaneContent = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, contentLeft, contentRight);

		add(splitPaneContent);
		this.txtInput.addKeyListener(this);
		this.btnSend.addActionListener(this);
		this.btnClear.addActionListener(this);

		append("<td colspan=3><i>Bitte klicken Sie links oben auf \"Verbinden\"...</i></td>");

		setSize(700, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		pack();
		setLocationRelativeTo(null);
		this.txtInput.setEditable(false);
		this.btnSend.setEnabled(false);
	}

	public void append(String s) {
		this.Outputtext += "<tr>" + s + "</tr>";
		this.txtOutput.setText("<html><body><table width='100%'>"
				+ this.Outputtext + "</table></body></html>");
		this.scrollDown = true;
		this.txtOutput.setText("<html><body><table width='100%'>"
				+ this.Outputtext + "</table></body></html>");
		this.scrollDown = true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == this.btnSend) {
			senden();
		} else if (src == this.btnClear) {
			this.Outputtext = " ";
			append("<td colspan=3><i>Konsole wurde gel\u00fcscht...</i></td>");
		}
	}

	public void senden() {
		String neu = this.txtInput.getText().trim();
		if (neu.length() > 0) {
			sendBroadcastTextMessage(neu);
			this.txtInput.setText("");
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

		if (e instanceof TextMessageEvent) {
			final TextMessageEvent txtME = (TextMessageEvent) e;
			String content = txtME.getText();
			append("<td valign='top' width='15%'><b>" + clientNameOf(senderID)
					+ ": </b></td><td width='85%'>" + content + "</td><td><i>"
					+ date + "</i></td>");

		} else if (e instanceof ReNameEvent) {
			final ReNameEvent rnE = (ReNameEvent) e;
			if (!this.clientNames.contains(e)) { // new join of somebody
				final ReNameEvent rne = (ReNameEvent) e;
				this.model.addElement(rnE.getName());

				this.clientNames.add(rne);
				append("<td valign='top' width='15%'><b>" + rne.getName()
						+ ": </b></td><td width='85%'>"
						+ " ist dem Chat beigetreten" + "</td><td><i>" + date
						+ "</i></td>");
			} else { // name change
				// not necessary yet
			}

		} else if (e instanceof ExitEvent) {
			final ExitEvent exE = (ExitEvent) e;
			// String exiter = clientNames.exE.getExiterID();// clientName with
			// id
			// model.removeElement(exiter);
			// append("<td valign='top' width='15%'><b>" + sender
			// + ": </b></td><td width='85%'>" + exiter
			// + " hat den Chat verlassen" + "</td><td><i>" + date
			// + "</i></td>");
		} else if (e instanceof TellIDEvent) {
			this.clientID = e.getReceiverID(); // my own id
		}
	}

	private String clientNameOf(int senderID) {
		for (ReNameEvent reNameEvent : this.clientNames) {
			System.out.println("ClientName: " + reNameEvent.getName());
			if (reNameEvent.getSenderID() == senderID) {
				return reNameEvent.getName();
			}
		}
		throw new RuntimeException("Name not found");

	}

	@Override
	public void connected(CommChannel source) {
		this.server = source;
		transmit(new ReNameEvent(this.clientID, ID_ALL, this.txtName.getText()));
		this.txtName.setEditable(false);
		this.setTitle("Verbunden");
		String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
//		append("<td valign='top' colspan=2><b>Sie wurden mit dem Server verbunden</b></td><td><i>"
//				+ date + "</i></td>");
		this.txtInput.setEditable(true);
		this.btnSend.setEnabled(true);
		this.btnSend.setEnabled(true);
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

		this.server = null; // let gc do its job
		this.clientID = 0;
		this.model.clear();
		this.txtInput.setEditable(false);
		this.btnSend.setEnabled(false);
		this.txtName.setEditable(true);
	}

	private void transmit(MessageEvent dataPacket) {
		this.server.transmit(dataPacket);
	}

	private void sendBroadcastTextMessage(String text) {
		transmit(new TextMessageEvent(this.clientID, ID_ALL, text));
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Object src = e.getSource();
		int key = e.getKeyCode();
		if (src == this.txtInput) {
			if (e.isShiftDown() && key == KeyEvent.VK_ENTER) {
				this.txtInput.append(System.getProperty("line.separator"));
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
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.scrollDown = false;
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
