package dialogs;

import javax.swing.*;

import org.checkerframework.checker.guieffect.qual.UIEffect;

import java.awt.*;

/**
 * Created by Nikhil Shinde on 5/3/2016.
 */
public class ConnectionLoad extends JDialog {

	private static final long serialVersionUID = -796468114271333260L;

	public static boolean load = true;

	@UIEffect
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

	@UIEffect
	public void showDialog() {
		setVisible(load);
	}
}