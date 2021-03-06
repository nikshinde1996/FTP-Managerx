package helperclasses;

import java.awt.*;

import org.checkerframework.checker.guieffect.qual.UIEffect;

/**
 * Created by Nikhil Shinde on 1/3/2016.
 */
public class GBC extends GridBagConstraints {

	private static final long serialVersionUID = 8143603136882803094L;

	@UIEffect
	public GBC(int gridx, int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
	}

	@UIEffect
	public GBC(int gridx, int gridy, int gridWidth, int gridHeight) {
		this.gridx = gridx;
		this.gridy = gridy;
		this.gridwidth = gridWidth;
		this.gridheight = gridHeight;
	}

	/* Must be annotated with @UIEffect ...*/
	public GBC setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	/* Must be annotated with @UIEffect ...*/
	public GBC setFill(int fill) {
		this.fill = fill;
		return this;
	}
	
	/* Must be annotated with @UIEffect ...*/
	public GBC setWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}

	/* Must be annotated with @UIEffect ...*/
	public GBC setInsets(int distance) {
		this.insets = new Insets(distance, distance, distance, distance);
		return this;
	}

	/* Must be annotated with @UIEffect ...*/
	public GBC setInsets(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
		return this;
	}

	/* Must be annotated with @UIEffect ...*/
	public GBC setIpad(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}
}