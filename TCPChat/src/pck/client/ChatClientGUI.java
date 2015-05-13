package pck.client;

import static pck.ChatProtocoll.ID_ALL;
import general.ClientPlugin;
import general.CommChannel;
import general.MessageEvent;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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

import pck.ExitEvent;
import pck.ReNameEvent;
import pck.TellIDEvent;
import pck.TextMessageEvent;

public class ChatClientGUI extends JFrame implements ActionListener,
		KeyListener, Receiver, MouseListener, WindowListener {
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
	private final JTextField txtName = new JTextField(
			System.getProperty("user.name"));
	private final JButton btnSend = new JButton("Senden");
	private final HTMLEditorKit kit = new HTMLEditorKit();
	private final HTMLDocument doc = new HTMLDocument();
	private String outputText = "<td colspan=3><b>Herzlich Willommen im Chat!</b><br></td>";
	boolean scrollDown = false;
	private final ArrayList<ReNameEvent> clientNames = new ArrayList<ReNameEvent>();

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

		append("Bitte klicken Sie links oben auf \"Verbinden\"...", null);

		setSize(700, 800);
		addWindowListener(this);
		setVisible(true);

		pack();
		setLocationRelativeTo(null);
		this.txtInput.setEditable(false);
		this.btnSend.setEnabled(false);
	}

	public void write(String s) {
		this.outputText += "<tr>" + s + "</tr>";
		this.txtOutput.setText("<html><body><table width='100%'>"
				+ this.outputText + "</table></body></html>");
		this.scrollDown = true;
	}

	public void append(String text, String source) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"HH:mm:ss");
		String time = sdf.format(new Date());

		if (source == null)
			write("<td colspan='3' valign='top'><i>" + text + "</i></td>");
		else
			write("<td  valign='top' width='15%'><b>" + source
					+ "</b></td><td width='75%'>" + text
					+ "</td><td align='right' width='8%'><i>" + time
					+ "</i></td>");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == this.btnSend) {
			sendMSG();
		} else if (src == this.btnClear) {
			this.outputText = " ";
			append("Konsole wurde gel\u00fcscht...", null);
		}
	}

	public void sendMSG() {
		String neu = this.txtInput.getText().trim();
		if (neu.length() > 0) {
			sendBroadcastTextMessage(neu);
			this.txtInput.setText("");
		}
	}

	@Override
	public void receiveNextMessage(MessageEvent e, CommChannel source) {

		System.out.println(e.getClass().getName());
		int senderID = e.getSenderID();
		// int receiverID = e.getReceiverID();

		if (e instanceof TextMessageEvent) {
			final TextMessageEvent txtME = (TextMessageEvent) e;
			String content = txtME.getText();
			append(content, clientNameOf(senderID));

		} else if (e instanceof ReNameEvent) {
			final ReNameEvent rnE = (ReNameEvent) e;
			if (!this.clientNames.contains(e)) { // new join of somebody
				this.clientNames.add((ReNameEvent) e);
				this.model.addElement(rnE.getName());

				append("<b>" + rnE.getName() + "</b> ist dem Chat beigetreten",
						null);
			}
		} else if (e instanceof ExitEvent) { // members leaves chat
			for (ReNameEvent ev : this.clientNames) {
				if (ev.getSenderID() == e.getReceiverID()) {
					append(ev.getName() + " hat den Chat verlassen", null);
					model.removeElement(ev.getName());
					clientNames.remove(ev);
				}
			}
		} else if (e instanceof TellIDEvent) {
			this.clientID = e.getReceiverID(); // my own id
			transmit(new ReNameEvent(this.clientID, ID_ALL,
					this.txtName.getText()));
			this.txtName.setEditable(false);
			this.txtInput.setEditable(true);
			this.btnSend.setEnabled(true);
			this.btnSend.setEnabled(true);
		}
	}

	private String clientNameOf(int senderID) {

		for (ReNameEvent e : this.clientNames) {
			System.out.println(e.getName() + " " + e.getSenderID());
			if (e.getSenderID() == senderID) {
				return e.getName();
			}
		}
		throw new IllegalArgumentException("No such client found, senderID: "
				+ senderID);
	}

	@Override
	public void connected(CommChannel source) {
		this.server = source;
		this.setTitle("Verbunden");
	}

	@Override
	public void disconnected(CommChannel source, boolean causedByOtherEnd) {
		if (causedByOtherEnd) {
			append("Die Serververbindung wurde unterbrochen...", null);
		} else {
			append("Sie haben sich vom Chat abgemeldet...", null);
		}
		write("<td colspan=3><b>Vielen Dank f\u00fcr die Nutzung des Chat Programms!</b><br><br>"
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
				sendMSG();
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

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			server.disconnect();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
