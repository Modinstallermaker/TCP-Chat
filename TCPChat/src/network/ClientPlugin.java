package network;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import layout.TableLayout;

public class ClientPlugin extends Container implements ActionListener, Receiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel lblIPAdress = new JLabel("IP Adresse");
	private final JLabel lblPort = new JLabel("Port");
	private final JTextField txtIP = new JTextField("localhost");
	private final JTextField txtPort = new JTextField("45678");
	private final JButton btnConnect = new JButton("Verbinden");
	private final JLabel lblStatus = new JLabel("Offline");
	private Connection server;
	private boolean connectionActive;
	public boolean causedbyme = false;
	private final Receiver receiver;
	private final Component parent;

	public ClientPlugin(Receiver receiver, Component parent) {
		this.receiver = receiver;
		this.parent = parent;
		this.btnConnect.addActionListener(this);

		final double txtf_Height = 20, lblHeight = 20, btnHeight = 30, gap = 10;
		double[] columns = { gap, 0.6, gap, 0.4, gap };
		double[] rows = { gap, lblHeight, 0, txtf_Height, gap, btnHeight, gap,
				lblHeight };

		setLayout(new TableLayout(new double[][] { columns, rows }));
		add(lblIPAdress, "1, 1");
		add(lblPort, "3, 1");
		add(txtIP, "1, 3");
		add(txtPort, "3, 3");
		add(btnConnect, "1,5, 3, 5");
		lblStatus.setFont(new Font(lblStatus.getFont().getFontName(),
				Font.BOLD, 16));
		add(lblStatus, "1, 7");

		setVisible(true);
	}

	private void connectToServer() {
		final String ip = txtIP.getText();

		try {
			final int port = Integer.parseInt(txtPort.getText());
			server = new Connection(new Socket(ip, port), this);

			txtIP.setEditable(false);
			txtPort.setEditable(false);
			connectionActive = true;
			btnConnect.setText("Verbindung trennen");
			lblStatus.setText("Online");
		} catch (IOException e) {
			showMessageDialog(
					parent,
					"Fehler beim Verbinden, bitte stellen Sie sicher, dass der Server existiert und IP Adresse und Port korrekt sind!");
		} catch (IllegalArgumentException e) {
			showMessageDialog(parent,
					"Bitte geben Sie einen g\u00fcltigen Port ein.");
		}
	}

	private void disconnectMannually() {
		try {
			causedbyme = true;
			server.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDisconnectedProperties();
	}

	private void setDisconnectedProperties() {
		server = null;
		txtIP.setEditable(true);
		txtPort.setEditable(true);
		connectionActive = false;
		lblStatus.setText("Offline");
		btnConnect.setText("Verbinden");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			if (connectionActive) {
				if (0 == showConfirmDialog(parent,
						"M\u00f6chten Sie die Verbindung wirklich trennen?"))
					disconnectMannually();
			} else
				connectToServer();
		}
	}

	@Override
	public void receiveNextDataPack(String msg, Connection source) {
		receiver.receiveNextDataPack(msg, source);
	}

	@Override
	public void connected(Connection source) {
		causedbyme = false;
		server = source;
		receiver.connected(source);
	}

	@Override
	public void disconnected(Connection source) {
		setDisconnectedProperties();
		receiver.disconnected(source);
	}

	public boolean connectionActive() {
		return this.connectionActive;
	}
}
