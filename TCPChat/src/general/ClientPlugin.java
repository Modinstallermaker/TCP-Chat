package general;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

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
	private CommChannel server;
	private boolean connectionActive;
	private final Receiver receiver;
	private final Component parent;

	public ClientPlugin(Receiver receiver, Component parent) {
		this.receiver = receiver;
		this.parent = parent;
		this.btnConnect.addActionListener(this);
		// this.txtIP.setColumns(10);
		// this.txtPort.setColumns(3);
		final double txtf_Height = 20, lblHeight = 20, btnHeight = 30, gap = 10;
		double[] columns = { gap, 0.6, gap, 0.4, gap };
		double[] rows = { gap, lblHeight, 0, txtf_Height, gap, btnHeight, gap,
				lblHeight };

		// for (double d : columns) {
		// val += d;
		// }
		//
		// for (int i = 0; i < columns.length; i++) {
		// columns[i] /= val;
		// }

		setLayout(new TableLayout(new double[][] { columns, rows }));
		add(this.lblIPAdress, "1, 1");
		add(this.lblPort, "3, 1");
		add(this.txtIP, "1, 3");
		add(this.txtPort, "3, 3");
		add(this.btnConnect, "1,5, 3, 5");
		this.lblStatus.setFont(new Font(this.lblStatus.getFont().getFontName(),
				Font.BOLD, 16));
		add(this.lblStatus, "1, 7");
		// setMinimumSize(getPreferredSize());
		// setMinimumSize(new Dimension(100, 200));
		setVisible(true);
	}

	private void connectToServer() {
		final String ip = this.txtIP.getText();

		try {
			final int port = Integer.parseInt(this.txtPort.getText());
			this.server = new CommChannel(new Socket(ip, port), this);

			this.txtIP.setEditable(false);
			this.txtPort.setEditable(false);
			this.connectionActive = true;
			this.btnConnect.setText("Verbindung trennen");
			this.lblStatus.setText("Online");
			// muss weg, woher kennt der client den server???
			// server.transmit(startDataPack(clientID));
		} catch (IOException e) {
			showMessageDialog(
					this.parent,
					"Fehler beim Verbinden, bitte stellen Sie sicher, dass der Server existiert und IP Adresse und Port korrekt sind!");

		} catch (IllegalArgumentException e) {
			showMessageDialog(this.parent,
					"Bitte geben Sie einen g\u00fcltigen Port ein.");

		}
	}

	private void disconnectMannually() {
		// muss weg, woher kennt der client den server???
		// server.transmit(exitDataPack(clientID));
		try {
			this.server.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDisconnectedProperties();
	}

	private void setDisconnectedProperties() {
		this.server = null;
		this.txtIP.setEditable(true);
		this.txtPort.setEditable(true);
		this.connectionActive = false;
		this.lblStatus.setText("Offline");
		this.btnConnect.setText("Verbinden");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnConnect) {
			if (this.connectionActive) {
				if (0 == showConfirmDialog(this.parent,
						"M\u00f6chten Sie die Verbindung wirklich trennen?"))
					disconnectMannually();
			} else {
				connectToServer();
			}
		}
	}

	@Override
	public void receiveNextMessage(MessageEvent msg, CommChannel source) {
		this.receiver.receiveNextMessage(msg, source);
	}

	@Override
	public void connected(CommChannel source) {
		this.server = Objects.requireNonNull(source);	//TODO	remove require
		this.receiver.connected(source);
	}

	@Override
	public void disconnected(CommChannel source, boolean causedByOtherEnd) {
		setDisconnectedProperties();
		this.receiver.disconnected(source, causedByOtherEnd);
	}

	public boolean connectionActive() {
		return this.connectionActive;
	}

	

}
