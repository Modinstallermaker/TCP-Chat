package general;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class CommChannel implements Runnable {
	private static int counter;
	private final Socket socket;
	private final int id;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private final Receiver receiver;

	// private final Scanner inputScanner;

	// private boolean shutdown = false;

	public CommChannel(Socket socket, Receiver receiver) throws IOException {
		this.id = ++counter;
		this.socket = socket;
		this.receiver = receiver;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		// this.inputScanner = new Scanner(in);
		// inputScanner.useDelimiter(Protocoll.DATA_PACK_END);
		new Thread(this, "Channel-" + this.id).start();

	}

	public void transmit(MessageEvent e) {

		try {
			this.out.writeObject(e);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * sends a message if the param id is equal to the intern id or if the id is
	 * zero
	 * 
	 * @param id
	 * @param msg
	 */

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
		boolean causedByOtherEnd = true;

		try {
			while (((msg = (MessageEvent) this.in.readObject())) != null) {
				this.receiver.receiveNextMessage(msg, this);
			}
		} catch (ClassNotFoundException e) { // shouldn't happen

			e.printStackTrace();
		} catch (SocketException e) { // own socket closed
			causedByOtherEnd = false;
		} catch (EOFException e) { // end of stream, connection interrupted
			causedByOtherEnd = true;
		}
		catch (IOException e) {
			// shouldn't happen
			e.printStackTrace();
		}

		this.receiver.disconnected(this, causedByOtherEnd);// ///////////////////////////////////////////
	}

}
