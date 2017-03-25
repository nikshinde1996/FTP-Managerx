package helperclasses;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.index.qual.LowerBoundUnknown;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Created by Nikhil Shinde on 1/3/2016.
 */
public class FileTreeModel implements TreeModel {
	protected File root;
	FileTreeFilter fileTreeFilter = new FileTreeFilter();

	public FileTreeModel(File root) {
		this.root = root;
	}

	@Override
	public @NonNull Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((File) node).isFile();
	}

	public @NonNegative int getChildCount(Object parent) {
		String[] children = ((File) parent).list(fileTreeFilter);
		if (children == null)
			return 0;
		return children.length;
	}

	/**
	 * The current type is @LowerBoundUnknown .... Should be @Positive
	 * or @NonNegative
	 */
	public @NonNull Object getChild(Object parent, @Positive int index) {
		String[] children = ((File) parent).list(fileTreeFilter);
		if ((children == null) || (index >= children.length))
			return null;
		return new File((File) parent, children[index]);
	}

	public int getIndexOfChild(Object parent, Object child) {
		String[] children = ((File) parent).list(fileTreeFilter);
		if (children == null)
			return -1;
		String childname = ((File) child).getName();
		for (int i = 0; i < children.length; i++) {
			if (childname.equals(children[i]))
				return i;
		}
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newvalue) {
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
	}

	class FileTreeFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return new File(dir, name).isDirectory();
		}
	}
}