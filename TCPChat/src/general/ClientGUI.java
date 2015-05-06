package general;

import javax.swing.JFrame;

public class ClientGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientGUI(String title) {
		super(title);
		ClientPlugin c = new ClientPlugin(null, this);
		setContentPane(c);		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocation(730, 400);
		setVisible(true);
	}
}