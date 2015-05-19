package general;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

public class CommChannel implements Runnable {
	private static int counter;
	private final Socket socket;
	private final int id;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private final Receiver receiver;

	public CommChannel(Socket socket, Receiver receiver) throws IOException {
		this.id = ++counter;
		this.socket = socket;
		this.receiver = receiver;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		new Thread(this, "Channel-" + this.id).start();

	}

	public void transmit(MessageEvent e) {
		try {
			this.out.writeObject(e);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}



	public void disconnect() throws IOException {
		this.socket.close();
	}

	public int getId() {
		return this.id;
	}

	@Override
	public void run() {
		this.receiver.connected(this);
		MessageEvent msg;

		try {
			while (((msg = (MessageEvent) this.in.readObject())) != null) {
				this.receiver.receiveNextMessage(msg, this);
			}
		} catch (ClassNotFoundException cnfexc) { // shouldn't happen
			cnfexc.printStackTrace();
		} catch (SocketException sexc) { // own or remote socket closed
		} catch (EOFException eofexc) { // end of stream, connection interrupted, why should this happen?
			eofexc.printStackTrace();
		}
		catch (IOException ioexc) {
			// shouldn't happen
			ioexc.printStackTrace();
		}

		this.receiver.disconnected(this, !this.socket.isClosed());// ///////////////////////////////////////////
	}

}
