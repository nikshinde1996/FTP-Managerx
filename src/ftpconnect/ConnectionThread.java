package ftpconnect;

import javax.swing.*;

/**
 * Created by Nikhil Shinde on 16/21/2016.
 */
public class ConnectionThread extends SwingWorker<Void, Void> {
    @Override
    protected Void doInBackground() throws Exception {
        ConnectHost.getLoginDetails();
        return null;
    }

    protected void done() {
        if (ConnectHost.hostConnected) {
            ClientMainFrame.loadDialog.dispose();
            JOptionPane.showMessageDialog(ClientMainFrame.frame, "Successfully logged in to the server", "Status: Connected", JOptionPane.OK_OPTION, new ImageIcon("res\\check_icon.png"));
        }
    }
}