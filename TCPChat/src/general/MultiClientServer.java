package general;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import layout.TableLayout;

public class MultiClientServer extends JFrame implements ActionListener, Receiver {
	/**{@link #action(java.awt.Event, Object)}
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel lblIPAdress = new JLabel("IP Adresse");
	private final JLabel lblPort = new JLabel("Port");
	private final JLabel lblIP = new JLabel();
	private final JLabel lblStatus = new JLabel("Server Offline");
	private final JTextField txtPort = new JTextField("45678");
	private final JButton btnServer = new JButton("Server Starten");

	private ServerSocket ss;
	private final List<Connection> clientList = new CopyOnWriteArrayList<>();
	private boolean connectionActive;
	protected final Receiver receiver;

	public MultiClientServer(String title, Receiver receiver) {
		super(title);
		this.receiver = receiver;
		buildGUI();	
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
		setLocation(800, 400);
		setVisible(true);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == btnServer) {
			if (connectionActive)
				destroyServer();			
			else 
				buildServer();
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
			} catch (IOException e) {
				e.printStackTrace();
				showMessageDialog(this,	"I/O Fehler beim Erstellen des Servers, port: " + port);
			}
		} catch (IllegalArgumentException e) {
			showMessageDialog(this,	"Bitte geben Sie einen g\u00fcltigen Port ein. z.B. 45678");
		}
	}

	private void waitForClients() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (connectionActive) {
					try {
						final Socket s = ss.accept();
						new Connection(s, MultiClientServer.this);
					} catch (SocketException e) {
						// ServerSocket wurde geschlossen
						ss = null; // let gc do its work
						} catch (IOException e) {
						e.printStackTrace();
						showMessageDialog(MultiClientServer.this, "Fehler beim Akzeptieren einer Verbindung");
					}
				}
			}
		}, "ClientAcceptor-Thread").start();
	}

	@Override
	public void connected(Connection source) {		
		clientList.add(source);
		receiver.connected(source);
		updateServerStatus();
	}

	@Override
	public void disconnected(Connection source) {	
		clientList.remove(source);
		receiver.disconnected(source);
		updateServerStatus();
	}

	private void updateServerStatus() {
		if (connectionActive) {
			final int clients = clientList.size();
			lblStatus.setText("Server Online, " + clients + " Client" + (clients != 1 ? "s angemeldet" : " angemeldet"));
			btnServer.setText("Server beenden");
		} 
		else { //Server ist offline
			try {
				kickAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void destroyServer() {
		if (clientList.isEmpty() || 0 == showConfirmDialog(this, "Sind Sie sicher, dass sie den Server beenden m\u00f6chten?"))
			try {
				kickAll();

				ss.close();
				ss = null;
				connectionActive = false;
				lblStatus.setText("Server Offline");
				txtPort.setEditable(true);
				btnServer.setText("Server starten");
			} catch (IOException e) {
				showMessageDialog(this,	"Fehler beim schlie\u00dfen des Servers");
			}
	}

	private void kickAll() throws IOException {
		for (Connection conn : clientList) {
			conn.disconnect();
		}
		clientList.clear();
	}

	public  List<Connection> getClientList() {
		return clientList;
	}

	@Override
	public void receiveNextDataPack(String data, Connection source) {
		receiver.receiveNextDataPack(data, source);
	}
}