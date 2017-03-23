package dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nikhil Shinde on 5/3/2016.
 */

public class ConnectionLoad extends JDialog {

	public static boolean load = true;

	public ConnectionLoad(JFrame parent) {
		super(parent, true);
		setUndecorated(true);
		setLocationRelativeTo(parent);
		ImageIcon ic = new ImageIcon("res\\712.gif");
		JLabel loading = new JLabel(ic);
		setBackground(new Color(255, 255, 255, 0));
		add(loading);
		pack();

	}

	public void showDialog() {
		setVisible(load);
	}
}
