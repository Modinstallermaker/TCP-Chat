package general;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import layout.TableLayout;

public class MultiClientServer extends JFrame implements ActionListener,
		Receiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel lblIPAdress = new JLabel("IP Adresse");
	private final JLabel lblPort = new JLabel("Port");
	// private final JLabel lblColon = new JLabel(":");
	private final JLabel lblIP = new JLabel();
	private final JLabel lblStatus = new JLabel("Server Offline");
	private final JTextField txtPort = new JTextField("45678");
	private final JButton btnServer = new JButton("Server Starten");

	private ServerSocket ss;
	private final List<CommChannel> clientList = new Vector<>();
	private boolean connectionActive;
	protected final Receiver receiver;

	public MultiClientServer(String title, Receiver receiver) {
		super(title);
		this.receiver = receiver;
		buildGUI();
		buildServer(); // remove after testing!!!!!!!

	}

	private void buildGUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			lblIP.setText(ip);

		} catch (UnknownHostException e) {
			lblIP.setText("IP unbekannt");
		}
		Container cont = new Container();
		btnServer.addActionListener(this);

		double[] columns = { 10, 80, 0, 50, 10, 100, 10 };
		double[] rows = { 10, 30, 30, 20, 20, 10 };
		int w = 0;
		for (double d : columns) {
			w += d;
		}

		for (int i = 0; i < columns.length; i++) {
			columns[i] /= w;
		}

		cont.setLayout(new TableLayout(new double[][] { columns, rows }));
		cont.add(lblIPAdress, "1, 1");
		cont.add(lblPort, "3, 1");
		cont.add(lblIP, "1, 2");
		cont.add(txtPort, "3, 2");
		cont.add(btnServer, "5, 2");
		cont.add(lblStatus, "1, 4, 5, 4");
		setContentPane(cont);
		// setLocationRelativeTo(null);
		setLocation(800, 400);
		setVisible(true);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == btnServer) {
			if (connectionActive) {
				destroyServer();
			} else {
				buildServer();

			}
		}
	}

	private void buildServer() {

		try {
			final int port = Integer.parseInt(txtPort.getText());
			try {
				ss = new ServerSocket(port);
				connectionActive = true;
				txtPort.setEditable(false);
				lblStatus.setText("Server Online, warte auf Clients...");
				btnServer.setText("Server beenden");
				waitForClients();
			} catch (BindException e) {
				e.printStackTrace();
				showMessageDialog(
						this,
						"Bindungsfehler beim Erstellen des Server auf Port "
								+ port
								+ ", dieser Port wird bereits von einem anderen Server verwendet. Versuchen Sie einen anderen Port.");

			} catch (IOException e) {
				e.printStackTrace();
				showMessageDialog(this,
						"I/O Fehler beim Erstellen des Servers, port: " + port);

			}
		} catch (IllegalArgumentException e) {
			showMessageDialog(this,
					"Bitte geben Sie einen g\u00fcltigen Port ein, empfohlener Bereich 45000-50000");
		}

	}

	private void waitForClients() {
		new Thread(new Runnable() {
			@Override
			public void run() {
					try {while (connectionActive) {
				
						final Socket s = ss.accept();
						new CommChannel(s, MultiClientServer.this);
					
					}
				} catch (SocketException e) {
					// ServerSocket wurde geschlossen
					ss = null; // let gc do its work
				} catch (IOException e) {
					e.printStackTrace();
					showMessageDialog(MultiClientServer.this,
							"I/O Fehler beim Akzeptieren einer Verbindung");
				}
			}

		}, "ClientAcceptor-Thread").start();
	}

	@Override
	public void connected(CommChannel source) {
		clientList.add(source);
		receiver.connected(source);
		updateServerStatus();
	}

	@Override
	public void disconnected(CommChannel source, boolean causedByClient) {
		clientList.remove(source);
		receiver.disconnected(source, causedByClient);
		 if (causedByClient) {
		updateServerStatus();
		 }
	}

	private void updateServerStatus() {
		if (connectionActive ) {
			final int clients = clientList.size();
			lblStatus.setText("Server Online, " + clients + " Client"
					+ (clients != 1 ? "s angemeldet" : " angemeldet"));
			btnServer.setText("Server beenden");
		} else {
			throw new UnsupportedOperationException(
					"this method should not be called when server is offline");
		}

	}

	private void destroyServer() {
		if (clientList.isEmpty()
				|| 0 == showConfirmDialog(this,
						"Sind Sie sicher, dass sie den Server beenden m\u00f6chten?"))
			try {
				kickAll();

				ss.close();
				ss = null;
				connectionActive = false;
				lblStatus.setText("Server Offline");
				txtPort.setEditable(true);
				btnServer.setText("Server starten");
			} catch (IOException e) {
				showMessageDialog(this,
						"Fehler beim schlie\u00dfen des Servers");
			}
	}

	private void kickAll() throws IOException {
		for (CommChannel conn : clientList) {
			conn.disconnect();
		}
		clientList.clear();
	}

	public List<CommChannel> getClientList() {
		return clientList;
	}

	@Override
	public void receiveNextMessage(MessageEvent data, CommChannel source) {
		receiver.receiveNextMessage(data, source);
	}
}