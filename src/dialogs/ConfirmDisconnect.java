package dialogs;

import javax.swing.*;

/**
 * Created by Nikhil Shinde on 4/3/2016.
 */
public class ConfirmDisconnect extends JOptionPane {
	private static int selection;

	public ConfirmDisconnect(JFrame parent, String message, String title) {
		selection = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, new ImageIcon("res\\alert_icon.png"));
	}

	public int getSelection() {
		return selection;
	}
}