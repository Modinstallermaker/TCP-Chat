package network;

import static network.Protocoll.DATA_PACK_END;
import static network.Protocoll.DATA_PACK_START;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Connection implements Runnable{
	private static int counter;
	private final Socket socket;
	private final int id;
	private final PrintStream out;
	private final Receiver receiver;
	private final Scanner inputScanner;

	public Connection(Socket socket, Receiver receiver) throws IOException {
		id = ++counter;
		this.socket = socket;
		this.receiver = receiver;
		
		out = new PrintStream(socket.getOutputStream(), true);		
		inputScanner = new Scanner(socket.getInputStream());
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
		socket.close();
	}

	public int getId() {
		return id;
	}

	@Override
	public void run() {
		receiver.connected(this);
		
		while (inputScanner.hasNext()) {
			String s = inputScanner.next();
			receiver.receiveNextDataPack(s, this);
			System.out.println("Datenpaket an Receiver gesendet: " + s);
		}	
		inputScanner.close();
		
		receiver.disconnected(this);	
	}

	private void sendAllCompletedMSGs(String dataPack) {

		if (true) {
			receiver.receiveNextDataPack(dataPack, this);
		}

	}
}
