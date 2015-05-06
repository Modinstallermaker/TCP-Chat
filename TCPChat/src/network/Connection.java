package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import static network.Protocoll.*;

public class Connection implements Runnable{
	private static int counter;
	private final Socket socket;
	private final int id;
	private final PrintStream out;
	private final InputStream in;
	private final Receiver receiver;
	private final Scanner inputScanner;

	// private boolean shutdown = false;

	public Connection(Socket socket, Receiver receiver) throws IOException {
		this.id = ++counter;
		this.socket = socket;
		this.receiver = receiver;
		this.out = new PrintStream(socket.getOutputStream(), true);
		this.in = socket.getInputStream();
		this.inputScanner = new Scanner(in);
		inputScanner.useDelimiter(Protocoll.DATA_PACK_END);
		new Thread(this, "Channel-" + id).start();

	}

	public void transmit(String msg) {
		String dataPack = DATA_PACK_START + msg + DATA_PACK_END;
		System.out.println("Übertrage: " + dataPack);
		try {
			this.out.write(dataPack.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sends a message if the param id is equal to the intern id or if the id is
	 * zero
	 * 
	 * @param id
	 * @param msg
	 */
	// public boolean sendMessageIfIDIs(int id, String msg) {
	// if (id == this.id) {
	// sendMessage(msg);
	// return true;
	// }
	// if (id < 0) {
	// sendMessage(msg);
	// }
	// return false;
	// }

	public void disconnect() throws IOException {
		// shutdown = true;
		socket.close();
	}

	public int getId() {
		return id;
	}

	@Override
	public void run() {
		receiver.connected(this);
//		String dataPack;
//		byte[] buffer = new byte[2048];
//		int length;

		while (inputScanner.hasNext()) {
			String s = inputScanner.next();
			receiver.receiveNextDataPack(s, this);
//			System.out.println("Datenpaket an Receiver gesendet: " + s);
		}
//		System.out.println("Der InputStream hat sein Ende erreicht!!!");
		inputScanner.close();
		boolean causedByOtherEnd = false;
		try {
			int read = in.read();
			causedByOtherEnd = true;
		} catch (SocketException e) {
			causedByOtherEnd = false;
		} catch (IOException e) {
			// shouldn't happen
			throw new InternalError("This shouldn't happen");
		}

		receiver.disconnected(this, causedByOtherEnd);
		// receiver.disconnected(this, causedByOtherEnd);
		// try {
		// while (true) {
		// length = in.read(buffer);
		// if (length < 0) {
		// // shutdown = true;
		// break; // other end has disconnected
		// } else {
		// dataPack = new String(buffer, 0, length);
		// sendMSGToReceiverIfCompleted(dataPack);
		// System.out.println("Empfänger benachrichgt");
		// }
		// }
		// this.receiver.disconnected(this, true);
		//
		// } catch (SocketException e) {
		// // System.out.println("Socket eigenverantwortlich geschlossen");
		// this.receiver.disconnected(this, false);
		// }
		//
		// catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	private void sendAllCompletedMSGs(String dataPack) {

		if (true) {
			receiver.receiveNextDataPack(dataPack, this);
		}

	}
}
