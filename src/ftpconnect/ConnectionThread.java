package ftpconnect;

import javax.swing.*;

import org.checkerframework.checker.guieffect.qual.UIEffect;

/**
 * Created by Nikhil Shinde on 7/3/2016.
 */
public class ConnectionThread extends SwingWorker<Void, Void> {
    @Override
    @UIEffect
    protected Void doInBackground() throws Exception {
        ConnectHost.getLoginDetails();
        return null;
    }

    @UIEffect
    protected void done() {
        if (ConnectHost.hostConnected) {
            ClientMainFrame.loadDialog.dispose();
            JOptionPane.showMessageDialog(ClientMainFrame.frame, "Successfully logged in to the server", "Status: Connected", JOptionPane.OK_OPTION, new ImageIcon("res\\check_icon.png"));
        }
    }
}