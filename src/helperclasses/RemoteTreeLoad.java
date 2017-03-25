package helperclasses;

import ftpconnect.ButtonActionListener;
import ftpconnect.ClientMainFrame;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.checkerframework.checker.guieffect.qual.UIEffect;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.IOException;

/**
 * Created by Nikhil Shinde on 1/3/2016.
 */
public class RemoteTreeLoad {

	public static FTPClient ftpClient;
	public static JTree remoteTree;
	private static DefaultMutableTreeNode dummyRoot;
	private static String currentPath;
	private static DefaultMutableTreeNode rootNode;
	public static DefaultTreeModel model;

	@UIEffect
	public RemoteTreeLoad(FTPClient ftp) {
		ftpClient = ftp;
		try {
			currentPath = ftpClient.printWorkingDirectory();
			FTPFile root = ftpClient.mlistFile(currentPath);
			rootNode = new DefaultMutableTreeNode(root.getName(), true);

			model = new DefaultTreeModel(rootNode);
			model.setAsksAllowsChildren(true);

			System.out.println("Retrieving remote file tree...");
			traverseRemoteTree(currentPath, rootNode);
			System.out.println("Successfully retrieved");

			FTPFile[] rootChildren = ftpClient.listFiles(currentPath, new FTPFileFilter() {
				@Override
				public boolean accept(FTPFile ftpFile) {
					return (ftpFile.getType() == 0);
				}
			});
			for (FTPFile rootChild : rootChildren)
				ClientMainFrame.defaultRemoteListModel.addElement(rootChild.getName());
			ClientMainFrame.remoteFileList.setSelectedIndex(0);
			remoteTree = new JTree(model);

			TreeSelection listener = new TreeSelection();
			remoteTree.addTreeSelectionListener(listener);
			remoteTree.setShowsRootHandles(true);
			remoteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			remoteTree.setSelectionPath(new TreePath(rootNode.getPath()));

			dummyRoot = rootNode;

			ClientMainFrame.remoteFileTreePane.setViewportView(remoteTree);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class TreeSelection implements TreeSelectionListener {

		@Override
		@UIEffect
		public void valueChanged(TreeSelectionEvent e) {

			if (remoteTree.getSelectionPath() != null) {
				TreePath path = remoteTree.getSelectionPath();
				dummyRoot = (DefaultMutableTreeNode) path.getLastPathComponent();

				TreePath treePath = new TreePath(dummyRoot.getPath());
				String pathDir = createFilePath(treePath);

				SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							ClientMainFrame.defaultRemoteListModel.removeAllElements();
							FTPFile[] files = ftpClient.listFiles(pathDir, new FTPFileFilter() {
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
							ClientMainFrame.remoteFileList.setSelectedIndex(0);
						} catch (IOException ie) {
							ie.printStackTrace();
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
	}

	@UIEffect
	public static void traverseRemoteTree(String path, DefaultMutableTreeNode tempRoot) {
		try {
			FTPFile[] children = ftpClient.listFiles(path);

			if (children.length != 0) {
				for (int i = 0; i < children.length; i++) {
					if (children[i].isDirectory()) {
						if (children[i].getName().equals(".") || children[i].getName().equals(".."))
							continue;
						DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(children[i].getName());
						tempRoot.add(dirNode);
						TreePath nodePath = new TreePath(dirNode.getPath());
						String currentPath = createFilePath(nodePath);
						traverseRemoteTree(currentPath, dirNode);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@UIEffect
	public static String createFilePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		ButtonActionListener.showServerReply(ftpClient);
		for (int i = 0; i < nodes.length; i++) {
			if (String.valueOf(nodes[i]).equals("/"))
				continue;
			sb.append("/").append(String.valueOf(nodes[i]));
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
}