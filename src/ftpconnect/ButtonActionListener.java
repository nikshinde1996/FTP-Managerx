package ftpconnect;

import dialogs.ConfirmDisconnect;
import helperclasses.RemoteTreeLoad;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.checkerframework.checker.guieffect.qual.UIEffect;
import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.linear.qual.Linear;
import org.checkerframework.checker.linear.qual.Unusable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;

/**
 * Created by Nikhil Shinde on 6/3/2016.
 */
@SuppressWarnings("initialization")
public class ButtonActionListener implements ActionListener {

	private int actionCode;
	private static @Localized String downloadLocalPath = "";
	private static @Localized String uploadSelectedPath = "";

	public ButtonActionListener(int actionCode) {
		this.actionCode = actionCode;
	}

	@UIEffect
	public void actionPerformed(ActionEvent ae) {
		switch (actionCode) {
		case 1:
			uploadLocalFile();
			break;
		case 2:
			newRemoteFolder();
			break;
		case 3:
			deleteRemoteFolder();
			break;
		case 4:
			downloadRemoteFile();
			break;
		case 5:
			deleteRemoteFile();
			break;
		case 6:
			renameRemoteFile();
			break;
		}
	}

	@UIEffect
	protected void uploadLocalFile() {
		String name = (String) ClientMainFrame.localFileList.getSelectedValue();
		@Linear
		TreePath remotePath = RemoteTreeLoad.remoteTree.getSelectionPath();
		@Localized
		String remoteSelected = createPath(remotePath) + "/" + name;

		TreePath localPath = FileSystemTree.tree.getSelectionPath();

		if (localPath == null) {
			@Unusable
			Path root = new File(System.getProperty("user.home")).toPath();
			root = root.getRoot();
			uploadSelectedPath = root.toString() + name;
		} else {
			uploadSelectedPath = ((File) localPath.getLastPathComponent()).getPath() + File.separator + name;
		}
		System.out.println(remoteSelected);
		System.out.println(uploadSelectedPath);

		SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try (FileInputStream inputStream = new FileInputStream(new File(uploadSelectedPath))) {
					if (!RemoteTreeLoad.ftpClient.storeFile(remoteSelected, inputStream)) {
						JOptionPane.showMessageDialog(ClientMainFrame.frame,
								"File couldn't be uploaded. Please try again.", "Upload unsuccessful",
								JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
					} else {
						JOptionPane.showMessageDialog(ClientMainFrame.frame, "File uploaded successfully to the server",
								"Upload Successful", JOptionPane.OK_OPTION, new ImageIcon("res\\check_icon.png"));

						ClientMainFrame.defaultRemoteListModel.removeAllElements();
						FTPFile[] files = RemoteTreeLoad.ftpClient.listFiles(createPath(remotePath),
								new FTPFileFilter() {
									@Override
									public boolean accept(FTPFile ftpFile) {
										return (!ftpFile.isDirectory());
									}
								});
						if (files.length == 0)
							ClientMainFrame.defaultRemoteListModel.removeAllElements();
						else {
							for (FTPFile file : files) {
								ClientMainFrame.defaultRemoteListModel.addElement(file.getName());
							}
						}
						ClientMainFrame.remoteFileList.setSelectedValue(name, true);
					}
				}
				return null;
			}

			protected void done() {
				ClientMainFrame.loadDialog.dispose();
			}
		};
		swingWorker.execute();
		ClientMainFrame.loadDialog.showDialog();
	}

	@UIEffect
	protected void deleteRemoteFolder() {
		if (RemoteTreeLoad.remoteTree.getSelectionPath() != null) {
			int selection = JOptionPane.showConfirmDialog(ClientMainFrame.frame,
					"Are you sure you want to permenantly delete this directory?", "Delete Directory",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon("res\\alert_icon.png"));

			if (selection == JOptionPane.OK_OPTION) {

				TreePath path = RemoteTreeLoad.remoteTree.getSelectionPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				String name = String.valueOf(node.getUserObject());

				String parentPath = createPath(path.getParentPath());
				String selectedPath = createPath(path);
				System.out.println(selectedPath + " " + parentPath);

				SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						if (removeDirectory(RemoteTreeLoad.ftpClient, parentPath, name)) {
							RemoteTreeLoad.model.removeNodeFromParent(node);
						} else {
							JOptionPane.showMessageDialog(ClientMainFrame.frame,
									"Couldn't delete the directory. Please try again.", "Operation failed",
									JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
						}
						return null;
					}

					protected void done() {
						ClientMainFrame.loadDialog.dispose();
					}
				};
				swingWorker.execute();
				ClientMainFrame.loadDialog.showDialog();
			} else {
				JOptionPane.showMessageDialog(ClientMainFrame.frame, "Choose a directory to delete", "Delete directory",
						JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
			}
		}
	}

	@UIEffect
	private boolean removeDirectory(FTPClient ftp, String parent, String current) throws IOException {
		String parentPath = parent;
		if (!current.equals("")) {
			parentPath += "/" + current;
		}

		FTPFile[] files = ftp.listFiles(parentPath);

		if (files != null && files.length > 0) {
			for (FTPFile file : files) {
				String currentFileName = file.getName();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					continue;
				}
				String filePath = parent + "/" + current + "/" + currentFileName;

				if (current.equals("")) {
					filePath = parent + "/" + currentFileName;
				}
				if (file.isDirectory()) {
					removeDirectory(ftp, parentPath, currentFileName);
				} else {
					if (ftp.deleteFile(filePath)) {
						System.out.println("Deleted the file: " + filePath);
					} else {
						System.out.println("Cannot delete the file: " + filePath);
					}
				}
			}
		}

		boolean removed = ftp.removeDirectory(parentPath);
		if (removed) {
			System.out.println("Removed the directory: " + parentPath);
			return true;
		} else {
			System.out.println("Cannot remove the directory: " + parentPath);
			return false;
		}
	}

	@UIEffect
	protected void newRemoteFolder() {
		String newName = (String) JOptionPane.showInputDialog(ClientMainFrame.frame, "Enter the name of new directory",
				"New Directory", JOptionPane.DEFAULT_OPTION, new ImageIcon("res\\bluealert.png"), null, null);
		if (newName != null) {
			System.out.println(newName);

			TreePath path = RemoteTreeLoad.remoteTree.getSelectionPath();
			String selectedPath = createPath(path) + "/" + newName;

			System.out.println(selectedPath);

			try {
				if (!RemoteTreeLoad.ftpClient.makeDirectory(selectedPath)) {
					JOptionPane.showMessageDialog(ClientMainFrame.frame,
							"Couldn't create new directory. Please try again", "Operation failed",
							JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
				} else {
					showServerReply(RemoteTreeLoad.ftpClient);
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) RemoteTreeLoad.remoteTree
							.getLastSelectedPathComponent();
					if (selectedNode == null)
						return;

					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newName);

					selectedNode.add(newNode);
					RemoteTreeLoad.model.reload(selectedNode);
					TreeNode[] nodes = RemoteTreeLoad.model.getPathToRoot(newNode);
					TreePath path1 = new TreePath(nodes);
					RemoteTreeLoad.remoteTree.scrollPathToVisible(path1);

					System.out.println("successful dir");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@UIEffect
	protected void downloadRemoteFile() {
		@NonNull
		String name = (String) ClientMainFrame.remoteFileList.getSelectedValue();
		TreePath path = RemoteTreeLoad.remoteTree.getSelectionPath();
		String selectedPath = createPath(path) + "/" + name;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}

			@Override
			public @Nullable String getDescription() {
				return null;
			}
		});
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("Select a directory to download");

		int returnValue = fileChooser.showOpenDialog(ClientMainFrame.frame);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			downloadLocalPath = fileChooser.getSelectedFile().getPath() + File.separator + name;

			SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {

					try (FileOutputStream fileOutputStream = new FileOutputStream(downloadLocalPath)) {
						if (!RemoteTreeLoad.ftpClient.retrieveFile(selectedPath, fileOutputStream)) {
							JOptionPane.showMessageDialog(ClientMainFrame.frame,
									"Download was unsuccessful. Please try again.", "Operation Failed",
									JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
							System.out.println("Download failed");
						} else {
							JOptionPane.showMessageDialog(ClientMainFrame.frame,
									"File has been successfully downloaded.", "Download successfull",
									JOptionPane.OK_OPTION, new ImageIcon("res\\check_icon.png"));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				protected void done() {
					ClientMainFrame.loadDialog.dispose();
				}
			};

			swingWorker.execute();
			ClientMainFrame.loadDialog.showDialog();
		}
	}

	@UIEffect
	protected void deleteRemoteFile() {

		int selection = new ConfirmDisconnect(ClientMainFrame.frame,
				"Are you sure you want to permanently delete this file?", "Delete file").getSelection();
		String name = (String) ClientMainFrame.remoteFileList.getSelectedValue();

		if (selection == JOptionPane.OK_OPTION) {
			int index = ClientMainFrame.remoteFileList.getSelectedIndex();
			try {
				TreePath path = RemoteTreeLoad.remoteTree.getSelectionPath();

				String selectedPath = createPath(path) + "/" + name;

				if (RemoteTreeLoad.ftpClient.deleteFile(selectedPath)) {
					System.out.println("Success");
					ClientMainFrame.defaultRemoteListModel.remove(index);
				} else {
					System.out.println("Failure");
					JOptionPane.showMessageDialog(ClientMainFrame.frame, "Operation failed. Try again.",
							"Operation failed", JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@UIEffect
	protected void renameRemoteFile() {
		String name = (String) ClientMainFrame.remoteFileList.getSelectedValue();
		int index = ClientMainFrame.remoteFileList.getSelectedIndex();
		try {
			@Initialized
			@NonNull
			String newName = (String) JOptionPane.showInputDialog(ClientMainFrame.frame, "Enter the new name:",
					"Rename", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("res\\bluealert.png"), null, null);
			String check = "";
			if (newName != null)
				check = newName.replaceAll(" ", "");

			if (newName != null) {
				while (newName == null || check.equals("")) {
					JOptionPane.showMessageDialog(ClientMainFrame.frame, "Empty field not allowed", "Invalid name",
							JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
					newName = (String) JOptionPane.showInputDialog(ClientMainFrame.frame, "Enter the new name:",
							"Rename", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("res\\bluealert.png"), null, null);
					if (newName != null)
						check = newName.replaceAll(" ", "");
				}
				TreePath path = RemoteTreeLoad.remoteTree.getSelectionPath();

				String selectedPath = createPath(path) + "/" + name;
				String toPath = createPath(path) + "/" + newName;

				if (RemoteTreeLoad.ftpClient.rename(selectedPath, toPath)) {
					System.out.println("Success");
					ClientMainFrame.defaultRemoteListModel.setElementAt((String) newName, index);
				} else {
					System.out.println("Failure");
					JOptionPane.showMessageDialog(ClientMainFrame.frame, "Operation failed. Try again.",
							"Operation failed", JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
				}
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(ClientMainFrame.frame, "Connection to server lost. Please try again.",
					"Connection Lost", JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
			e.printStackTrace();
		}

	}

	@UIEffect
	public static String createPath(TreePath node) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = node.getPath();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].toString().equals("/"))
				continue;
			sb.append("/").append(nodes[i].toString());
		}
		return sb.toString();
	}

	@UIEffect
	public static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				System.out.println("SERVER: " + aReply);
			}
		}
	}
}