package general;

import static general.Protocoll.DATA_PACK_END;
import static general.Protocoll.DATA_PACK_START;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class CommChannel implements Runnable{
	private static int counter;
	private final Socket socket;
	private final int id;
	private final PrintStream out;
	private final Receiver receiver;
	private final Scanner inputScanner;

	public CommChannel(Socket socket, Receiver receiver) throws IOException {
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
			out.write(dataPack.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
		}	
		inputScanner.close();
		
		receiver.disconnected(this);	
	}
}
