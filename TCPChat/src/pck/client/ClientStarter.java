package pck.client;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class ClientStarter {
	public static void main(String[] args) {		 
		 try {
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
	     } 
		 catch (ClassNotFoundException ce) {             
		 }
		 catch (InstantiationException ie) {           
		 }
		 catch (IllegalAccessException iae) {         
		 }
		 catch (UnsupportedLookAndFeelException e) {			
		 } 
		 new ChatClientGUI();
	}
}
