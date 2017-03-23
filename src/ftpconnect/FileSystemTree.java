package ftpconnect;

import helperclasses.FileTreeModel;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Nikhil Shinde on 17/21/2016.
 */
public class FileSystemTree {

	protected List<File> list;
	protected static JTree tree;

	protected static JTree getFileSystemTree(Path path) {

		FileTreeModel model = new FileTreeModel(path.toFile());
		JTree tree = new JTree(model);
		tree.setRootVisible(true);

		return tree;
	}

	protected static JTree getFileSystemFromRoot() {
		Path path = new File(System.getProperty("user.home")).toPath();
		path = path.getRoot();

		FileTreeModel model = new FileTreeModel(path.toFile());
		tree = new JTree(model);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		return tree;
	}

	protected static File[] getPartitions() {
		File[] partitions = File.listRoots();
		return partitions;
	}

	protected static File[] getRootFiles() {
		Path path = new File(System.getProperty("user.home")).toPath();
		path = path.getRoot();
		try {
			FileTraverse.triggerTraverse(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File[] rootFiles = FileTraverse.fileList.toArray(new File[FileTraverse.fileList.size()]);
		return rootFiles;
	}

	protected static File[] getPathFiles(Path path) {
		try {
			FileTraverse.triggerTraverse(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File[] partitionFiles = FileTraverse.fileList.toArray(new File[FileTraverse.fileList.size()]);
		return partitionFiles;
	}
}