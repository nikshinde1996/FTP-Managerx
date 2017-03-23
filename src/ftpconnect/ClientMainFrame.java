package ftpconnect;

import dialogs.ConfirmDisconnect;
import dialogs.ConnectionLoad;
import helperclasses.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.*;

import org.checkerframework.checker.guieffect.qual.UIEffect;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by Nikhil Shinde on 4/3/2016.
 */

public class ClientMainFrame extends JFrame {

	private static final long serialVersionUID = 4203465929082638519L;

	public static JFrame frame;

	// Dimensions
	private static int FIELD_WIDTH;
	private static int FRAME_WIDTH;
	private static int FRAME_HEIGHT;
	private static int LIST_WIDTH;
	private static int LIST_HEIGHT;
	private static int PANE_WIDTH;
	private static int PANE_HEIGHT;

	public static ConnectionLoad loadDialog;

	private JPanel fieldPanel, localFilePanel, remoteFilePanel;

	// fieldPanel instances
	private static JTree localTree;
	protected static JFormattedTextField ipField;
	protected static JFormattedTextField portField;
	protected static JTextField usernameField;
	protected static JPasswordField passwordField;
	protected static JButton connectButton, disconnectButton;

	// localFilePanel instances
	private static JPanel localFileTreePanel, localFileListPanel;
	private static DefaultListModel defaultLocalListModel = new DefaultListModel();
	protected static JList<Object> localFileList;
	protected static JButton uploadFile;

	// remoteFilePanel instances
	public static JTree remoteTree;
	private static JPanel remoteFileTreePanel, remoteFileListPanel;
	private static JButton newFolder, deleteFolder, downloadFile, renameFile, deleteFile;
	public static JScrollPane remoteFileTreePane;
	public static DefaultListModel defaultRemoteListModel = new DefaultListModel();
	public static JList<Object> remoteFileList;

	// button action constants
	private static final int UPLOAD_ACTION = 1;
	private static final int NEW_DIR_ACTION = 2;
	private static final int DELETE_DIR_ACTION = 3;
	private static final int DOWNLOAD_FILE_ACTION = 4;
	private static final int DELETE_FILE_ACTION = 5;
	private static final int RENAME_FILE_ACTION = 6;

	@UIEffect
	public ClientMainFrame() {

		initDimensions();
		setTitle("FTP Client");
		setLayout(new GridBagLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setMenuBar();

		setFieldPanel();
		add(fieldPanel, new GBC(0, 0).setWeight(0, 0.06).setFill(GBC.BOTH));

		setLocalFilePanel();
		add(localFilePanel, new GBC(0, 1).setWeight(0, 0.47).setFill(GBC.BOTH));

		setRemoteFilePanel();
		add(remoteFilePanel, new GBC(0, 2).setWeight(0, 0.47).setFill(GBC.BOTH));

		disableRemote();
		pack();
	}

	@UIEffect
	private void initDimensions() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenDimen = toolkit.getScreenSize();

		FRAME_HEIGHT = screenDimen.height;
		FRAME_WIDTH = screenDimen.width;

		PANE_WIDTH = (int) (FRAME_WIDTH * 0.2292);
		PANE_HEIGHT = (int) (FRAME_HEIGHT * 0.25463);

		LIST_WIDTH = (int) (FRAME_WIDTH * 0.2448);
		LIST_HEIGHT = (int) (FRAME_HEIGHT * 0.25465);

		FIELD_WIDTH = (int) (FRAME_WIDTH * 0.0068);
	}

	@UIEffect
	private void setFieldPanel() {
		fieldPanel = new JPanel();
		fieldPanel.setBorder(BorderFactory.createTitledBorder("FTP Connect"));

		ipField = new JFormattedTextField();
		ipField.setToolTipText("IP Address or Host Name");
		ipField.setColumns(FIELD_WIDTH);

		portField = new JFormattedTextField();
		portField.setColumns(FIELD_WIDTH - 7);

		usernameField = new JTextField();
		usernameField.setColumns(FIELD_WIDTH);

		passwordField = new JPasswordField();
		passwordField.setColumns(FIELD_WIDTH);

		addField(ipField, "Host:");
		addField(portField, "Port:");
		addField(usernameField, "Username:");
		addField(passwordField, "Password:");

		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ipField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Enter a host name to connect", "Sign in",
							JOptionPane.OK_OPTION, new ImageIcon("res\\alert_icon.png"));
				} else {
					ConnectionThread ct = new ConnectionThread();
					ct.execute();

					loadDialog = new ConnectionLoad(ClientMainFrame.frame);
					loadDialog.showDialog();
				}
			}
		});
		disconnectButton = new JButton("Disconnect");
		disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int selection = new ConfirmDisconnect(frame, "Are you sure you want to disconnect the session ?",
						"Exit").getSelection();
				if (selection == JOptionPane.OK_OPTION) {
					ConnectHost.disconnectSession();
				}
			}
		});
		fieldPanel.add(connectButton);
		fieldPanel.add(disconnectButton);
	}

	@UIEffect
	private void addField(JTextComponent component, String label) {
		fieldPanel.add(new JLabel(label));
		fieldPanel.add(component);
	}

	@UIEffect
	private void setLocalFilePanel() {
		localFilePanel = new JPanel();
		localFilePanel.setBorder(BorderFactory.createTitledBorder("Local Files"));

		populateLocalFileTreePanel();
		populateLocalFileListPanel();

		localFilePanel.add(localFileTreePanel, BorderLayout.WEST);
		localFilePanel.add(localFileListPanel, BorderLayout.EAST);
	}

	@UIEffect
	private void populateLocalFileTreePanel() {
		localFileTreePanel = new JPanel();
		localFileTreePanel.setLayout(new GridBagLayout());
		localFileList = new JList<>(defaultLocalListModel);

		File[] rootFiles = FileSystemTree.getRootFiles();

		for (File obj : rootFiles) {
			defaultLocalListModel.addElement(obj.getName());
		}
		localFileList.setSelectedIndex(0);

		JComboBox<File> partitions = new JComboBox<>(FileSystemTree.getPartitions());
		partitions.setSelectedIndex(0);
		partitions.setPreferredSize(new Dimension(100, 25));

		partitions.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				FileTreeModel model = new FileTreeModel((File) partitions.getSelectedItem());
				File[] partitionFiles = FileSystemTree.getPathFiles(((File) partitions.getSelectedItem()).toPath());
				defaultLocalListModel.clear();
				for (File obj : partitionFiles) {
					defaultLocalListModel.addElement(obj.getName());
				}
				localFileList.setSelectedIndex(0);
				localTree.setModel(model);
			}
		});

		localFileTreePanel.add(partitions, new GBC(0, 0).setAnchor(GBC.WEST).setInsets(0, 0, 5, 0));

		localTree = FileSystemTree.getFileSystemFromRoot();

		JScrollPane localFileTreePane = new JScrollPane(localTree);
		localFileTreePane.setPreferredSize(new Dimension(PANE_WIDTH, PANE_HEIGHT));
		TreeListener listener = new TreeListener();
		localTree.addTreeSelectionListener(listener);

		localFileTreePanel.add(localFileTreePane, new GBC(0, 1));
	}

	
	class TreeListener implements TreeSelectionListener {

		@UIEffect
		public void valueChanged(TreeSelectionEvent te) {
			if (localTree.getLastSelectedPathComponent() == null) {
				Path path = new File(System.getProperty("user.home")).toPath().getRoot();

				try {
					FileTraverse.triggerTraverse(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
				defaultLocalListModel.clear();
				for (File obj : FileTraverse.fileList) {
					defaultLocalListModel.addElement(obj.getName());
				}
				localFileList.setModel(defaultLocalListModel);
				localFileList.setSelectedIndex(0);
			} else {
				String path = localTree.getLastSelectedPathComponent().toString();
				Path filePath = FileSystems.getDefault().getPath(path);
				try {
					FileTraverse.triggerTraverse(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				defaultLocalListModel.clear();
				for (File obj : FileTraverse.fileList) {
					defaultLocalListModel.addElement(obj.getName());
				}
				localFileList.setModel(defaultLocalListModel);
				localFileList.setSelectedIndex(0);
			}
		}
	}

	@UIEffect
	private void populateLocalFileListPanel() {
		localFileListPanel = new JPanel();
		localFileListPanel.setLayout(new GridBagLayout());

		JScrollPane scrollPaneList = new JScrollPane(localFileList);
		scrollPaneList.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		localFileListPanel.add(scrollPaneList, new GBC(0, 0).setFill(GBC.HORIZONTAL));

		uploadFile = new JButton("Upload Selected File");
		uploadFile.addActionListener(new ButtonActionListener(UPLOAD_ACTION));

		localFileListPanel.add(uploadFile, new GBC(0, 1).setFill(GBC.HORIZONTAL).setInsets(5, 0, 0, 0));
	}
	
	@UIEffect
	private void setRemoteFilePanel() {
		remoteFilePanel = new JPanel();
		remoteFilePanel.setBorder(BorderFactory.createTitledBorder("Remote Files"));

		populateRemoteFileTreePanel();
		populateRemoteFileListPanel();

		addPanel(remoteFileTreePanel, remoteFilePanel);
		addPanel(remoteFileListPanel, remoteFilePanel);
	}

	@UIEffect
	private void populateRemoteFileTreePanel() {
		remoteFileTreePanel = new JPanel();
		remoteFileTreePanel.setLayout(new GridBagLayout());
		remoteFileList = new JList<>(defaultRemoteListModel);

		// Null tree for empty display when not connected
		remoteTree = new JTree();
		remoteTree.setModel(null);

		remoteFileTreePane = new JScrollPane(remoteTree);
		remoteFileTreePane.setPreferredSize(new Dimension(PANE_WIDTH, PANE_HEIGHT));
		remoteFileTreePanel.add(remoteFileTreePane, new GBC(0, 0, 2, 1));

		newFolder = new JButton("New Folder");
		newFolder.addActionListener(new ButtonActionListener(NEW_DIR_ACTION));
		remoteFileTreePanel.add(newFolder,
				new GBC(0, 1, 1, 1).setFill(GBC.HORIZONTAL).setWeight(0.5, 0).setInsets(5, 0, 0, 5));

		deleteFolder = new JButton("Delete Folder");
		deleteFolder.addActionListener(new ButtonActionListener(DELETE_DIR_ACTION));
		remoteFileTreePanel.add(deleteFolder,
				new GBC(1, 1, 1, 1).setFill(GBC.HORIZONTAL).setWeight(0.5, 0).setInsets(5, 0, 0, 0));
	}

	@UIEffect
	private void populateRemoteFileListPanel() {
		remoteFileListPanel = new JPanel();
		remoteFileListPanel.setLayout(new GridBagLayout());

		JScrollPane scrollPaneList = new JScrollPane(remoteFileList);
		scrollPaneList.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		remoteFileListPanel.add(scrollPaneList, new GBC(0, 0, 3, 1).setFill(GBC.HORIZONTAL));

		downloadFile = new JButton("Download File");
		downloadFile.addActionListener(new ButtonActionListener(DOWNLOAD_FILE_ACTION));
		remoteFileListPanel.add(downloadFile,
				new GBC(0, 1, 1, 1).setFill(GBC.HORIZONTAL).setWeight(0.33, 0).setInsets(5, 0, 0, 5));

		deleteFile = new JButton("Delete File");
		deleteFile.addActionListener(new ButtonActionListener(DELETE_FILE_ACTION));
		remoteFileListPanel.add(deleteFile,
				new GBC(1, 1, 1, 1).setFill(GBC.HORIZONTAL).setWeight(0.33, 0).setInsets(5, 0, 0, 5));

		renameFile = new JButton("Rename File");
		renameFile.addActionListener(new ButtonActionListener(RENAME_FILE_ACTION));
		remoteFileListPanel.add(renameFile,
				new GBC(2, 1, 1, 1).setFill(GBC.HORIZONTAL).setWeight(0.33, 0).setInsets(5, 0, 0, 0));
	}

	@UIEffect
	protected void addPanel(JPanel childPanel, JPanel parentPanel) {
		parentPanel.add(childPanel);
	}

	@UIEffect
	private void setMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu file = new JMenu("File");
		menuBar.add(file);
		JMenu edit = new JMenu("Edit");
		menuBar.add(edit);
		JMenu help = new JMenu("Help");
		menuBar.add(help);

		JMenuItem newSession = new JMenuItem("New");

		file.add(newSession);
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (new ConfirmDisconnect(frame, "Are you aure you want to terminate this session?",
						"Terminate session").getSelection() == 0) {
					frame.dispose();
					// Default connections close operations....
				}
			}
		});
		file.add(exit);

		// Edit MenuItems
		JMenuItem theme = new JMenuItem("Themes");
		edit.add(theme);
		JMenuItem settings = new JMenuItem("Settings");
		edit.add(settings);

		// Help MenuItems
		JMenuItem aboutUs = new JMenuItem("About Us");
		help.add(aboutUs);
		JMenuItem checkUpdate = new JMenuItem("Check for Update");
		help.add(checkUpdate);
	}

	@UIEffect
	protected static void disableRemote() {
		if (!ConnectHost.hostConnected) {
			connectButton.setEnabled(true);
			disconnectButton.setEnabled(false);

			uploadFile.setEnabled(false);

			remoteFileTreePane.setViewportView(remoteTree);
			remoteTree.setEnabled(false);
			remoteFileList.setEnabled(false);
			defaultRemoteListModel.removeAllElements();

			newFolder.setEnabled(false);
			deleteFolder.setEnabled(false);
			downloadFile.setEnabled(false);
			renameFile.setEnabled(false);
			deleteFile.setEnabled(false);
		}
	}

	@UIEffect
	protected static void enableRemote() {
		connectButton.setEnabled(false);
		disconnectButton.setEnabled(true);

		uploadFile.setEnabled(true);

		remoteFileList.setEnabled(true);

		newFolder.setEnabled(true);
		deleteFolder.setEnabled(true);
		downloadFile.setEnabled(true);
		renameFile.setEnabled(true);
		deleteFile.setEnabled(true);
	}

	@UIEffect
	protected static void resetFields() {
		ipField.setText("");
		portField.setText("");
		usernameField.setText("");
		passwordField.setText("");
	}

	@UIEffect
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new ClientMainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
