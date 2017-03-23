package ftpconnect;

import helperclasses.RemoteTreeLoad;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

/**
 * Created by Nikhil Shinde on 5/3/2016.
 */
public class ConnectHost {

    protected static String username;
    protected static String port;
    protected static String password;
    private static InetAddress ipaddress;

    protected static boolean hostConnected = false;
    private static FTPClient ftpClient;

    protected static void getLoginDetails() {
        String ip_address = ClientMainFrame.ipField.getText().trim();
        username = ClientMainFrame.usernameField.getText().trim();
        port = ClientMainFrame.portField.getText().trim();
        password = String.valueOf(ClientMainFrame.passwordField.getPassword());
        if (resolveIPAddress(ip_address)) {
            if (FTPConnect()) {
                hostConnected = true;
                new RemoteTreeLoad(ftpClient);
                modifyClientMainFrame();
            }
        } else {
            ClientMainFrame.resetFields();
        }
    }

    private static void modifyClientMainFrame() {
        ClientMainFrame.frame.setTitle("FTP Client: Connected");
        ClientMainFrame.resetFields();
        ClientMainFrame.enableRemote();
    }

    private static boolean resolveIPAddress(String ip_address) {
        try {
            ipaddress = InetAddress.getByName(ip_address);
            System.out.println(ipaddress.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ClientMainFrame.frame, "Enter a valid host name or check your internet connection.", "Unable to resolve host", JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
            ClientMainFrame.loadDialog.dispose();
            return false;
        }
        return true;
    }

    private static boolean FTPConnect() {
        ftpClient = new FTPClient();
        try {
            if (port.isEmpty()) ftpClient.connect(ipaddress);
            else ftpClient.connect(ipaddress, Integer.valueOf(port));

            System.out.println("Connecting to the server...");
            System.out.println(ftpClient.getReplyString());
            int responseCode = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(responseCode)) {
                ftpClient.disconnect();
                System.err.println("FTP server refused connection. Reply Code: " + responseCode);
                return false;
            }
            boolean connected = ftpClient.login(username, password);
            serverReplies(ftpClient);

            if (!connected) {
                JOptionPane.showMessageDialog(ClientMainFrame.frame, "Invalid credentials. Please try again.", "Authentication Failed", JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
                System.out.println("Couldn't connect to server");
                ClientMainFrame.loadDialog.dispose();
                return false;
            } else {
                System.out.println("Successfully connected to server");
                return true;
            }
        } catch (ConnectException ce) {
            ClientMainFrame.loadDialog.dispose();
            JOptionPane.showMessageDialog(ClientMainFrame.frame, "Check your connection and try again", "Connection Timed Out", JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
            ClientMainFrame.resetFields();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void serverReplies(FTPClient ftp) {
        String[] replies = ftp.getReplyStrings();
        if (replies != null) {
            for (String reply : replies) System.out.println("Server: " + reply);
        }
    }

    protected static void disconnectSession() {
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            hostConnected = false;
            ClientMainFrame.frame.setTitle("FTP Client");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully disconnected from server");
        ClientMainFrame.disableRemote();
    }
}
