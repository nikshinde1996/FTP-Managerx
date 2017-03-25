package dialogs;

import javax.swing.*;

import org.checkerframework.checker.guieffect.qual.UIEffect;

/**
 * Created by Nikhil Shinde on 4/3/2016.
 */
public class ConfirmDisconnect extends JOptionPane {

	private static final long serialVersionUID = -7715352496018193903L;

	private static int selection;

	@UIEffect
	public ConfirmDisconnect(JFrame parent, String message, String title) {
		selection = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, new ImageIcon("res\\alert_icon.png"));
	}

	public int getSelection() {
		return selection;
	}
}