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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;

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
	private final Set<CommChannel> clients = Collections
			.synchronizedSet(new CopyOnWriteArraySet<CommChannel>());

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
			this.lblIP.setText(ip);

		} catch (UnknownHostException e) {
			this.lblIP.setText("IP unbekannt");
		}

		final Container cont = new Container();
		this.btnServer.addActionListener(this);

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
		cont.add(this.lblIPAdress, "1, 1");
		cont.add(this.lblPort, "3, 1");
		cont.add(this.lblIP, "1, 2");
		cont.add(this.txtPort, "3, 2");
		cont.add(this.btnServer, "5, 2");
		cont.add(this.lblStatus, "1, 4, 5, 4");
		setContentPane(cont);
		setLocation(800, 400);
		setVisible(true);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == this.btnServer) {
			if (this.connectionActive) {
				destroyServer();
			} else {
				buildServer();

			}
		}
	}

	private void buildServer() {

		try {
			final int port = Integer.parseInt(this.txtPort.getText());
			try {
				this.ss = new ServerSocket(port);
				this.connectionActive = true;
				this.txtPort.setEditable(false);
				this.lblStatus.setText("Server Online, warte auf Clients...");
				this.btnServer.setText("Server beenden");
				waitForClients();
			} catch (BindException e) {
				e.printStackTrace();
				showMessageDialog(
						this,
						"Bindungsfehler beim Erstellen des Server auf Port "
								+ port
								+ ", dieser Port wird bereits von einem anderen Server verwendet. Versuchen Sie einen anderen Port, empfohlener Bereich 45000-50000.");

			} catch (IOException e) {
				e.printStackTrace();
				showMessageDialog(this,
						"I/O Fehler beim Erstellen des Servers, port: " + port);

			}
		} catch (IllegalArgumentException e) {
			showMessageDialog(
					this,
					"Bitte geben Sie einen g\u00fcltigen Port ein, empfohlener Bereich 45000-50000.");
		}

	}

	private void waitForClients() {
		new Thread(new Runnable() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				try {
					while (MultiClientServer.this.connectionActive) {
						final Socket s = MultiClientServer.this.ss.accept();
						new CommChannel(s, MultiClientServer.this);
					}
				} catch (SocketException e) {
					// ServerSocket wurde geschlossen
					MultiClientServer.this.ss = null; // let gc do its work
				} catch (IOException e) {
					e.printStackTrace();
					showMessageDialog(
							MultiClientServer.this,
							"I/O Fehler beim Akzeptieren einer Verbindung "
									+ e.getMessage());
				}
			}

		}, "ClientAcceptor-Thread").start();
	}

	@Override
	public void connected(CommChannel source) {
		this.clients.add(source);
		this.receiver.connected(source);
		updateServerStatus();
	}

	@Override
	public void disconnected(CommChannel source, boolean causedByClient) {
		if (this.clients.remove(source)) {
			System.out.println("Channel entfernt");
		} else {
			throw new RuntimeException("This shouldn't happen");
		}
		this.receiver.disconnected(source, causedByClient);
		if (causedByClient) {
			updateServerStatus();
		}else {
			System.out.println("Verbindung wurde angeblich vom Server unterbrochen");
		}
	}

	private void updateServerStatus() {
		if (this.connectionActive) {
			final int clients = this.clients.size();
			this.lblStatus.setText("Server Online, " + clients + " Client"
					+ (clients != 1 ? "s angemeldet" : " angemeldet"));
			this.btnServer.setText("Server beenden");
		} else {
			throw new UnsupportedOperationException(
					"this method should not be called when server is offline");
		}

	}

	private void destroyServer() {
		if (this.clients.isEmpty()
				|| 0 == showConfirmDialog(this,
						"Sind Sie sicher, dass sie den Server beenden m\u00f6chten?"))
			try {
				kickAll();

				this.ss.close();
				this.ss = null;
				this.connectionActive = false;
				this.lblStatus.setText("Server Offline");
				this.txtPort.setEditable(true);
				this.btnServer.setText("Server starten");
			} catch (IOException e) {
				showMessageDialog(this,
						"Fehler beim schlie\u00dfen des Servers");
			}
		
	}

	private void kickAll() throws IOException {
		for (CommChannel conn : this.clients) {
			conn.disconnect();

		}
	}

	public Set<CommChannel> getClients() {
		return this.clients;
	}

	@Override
	public void receiveNextMessage(MessageEvent data, CommChannel source) {
		this.receiver.receiveNextMessage(data, source);
	}
}