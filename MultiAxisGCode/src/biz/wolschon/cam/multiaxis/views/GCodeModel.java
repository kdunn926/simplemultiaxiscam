package biz.wolschon.cam.multiaxis.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import biz.wolschon.cam.multiaxis.tools.Tool;

/**
 * Custom list model that is type-safe for storing only {@link GCodeLine}s.
 * @author marcuswolschon
 *
 */
public class GCodeModel extends javax.swing.AbstractListModel {

	/**
	 * For {@link Serializable}.
	 */
	private static final long serialVersionUID = -2039113947665731751L;
	/**
	 * Helper-class for the ListModel.
	 * Wraps a line of G-Code to display and the corresponding tool-location.
	 */
	public static class GCodeLine {
		private String mLine;
		private double[] mToolLocation;
		private Tool mTool;
		public Tool getTool() {
			return mTool;
		}
		public GCodeLine (final String aLine, final double[] aToolLocation, final Tool aTool) {
			this.mLine = aLine;
			this.mTool = aTool;
			if (aToolLocation != null) {
				this.mToolLocation = Arrays.copyOf(aToolLocation, aToolLocation.length);
			}
		}
		/**
		 * 
		 * @return May be null in header
		 */
		public double[] getToolLocation() {
			return mToolLocation;
		}
		@Override
		public String toString() {
			return mLine;
		}
	}
	private List<GCodeLine> mGCodeLines = new ArrayList<GCodeLine>();
	public double[] getToolLocation(int anIndex) {
		GCodeLine line = getElementAt(anIndex);
		return line.getToolLocation();
	}
	@Override
	public GCodeLine getElementAt(int anIndex) {
		return mGCodeLines.get(anIndex);
	}
	@Override
	public int getSize() {
		return mGCodeLines.size();
	}
	public void addElement(final GCodeLine aGCodeLine) {
		this.mGCodeLines.add(aGCodeLine);
		fireIntervalAdded(this, getSize() - 1, getSize());
	}
	public void clear() {
		this.mGCodeLines.clear();
		fireContentsChanged(this, 0, 0);		
	}

}
