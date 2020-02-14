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
		/**
		 * The actual text of the line of G-Code.
		 */
		private String mLine;
		/**
		 * Location of every machine axis after performing this move.
		 */
		private double[] mToolLocation;
		/**
		 * The active tool during this step.
		 */
		private Tool mTool;
		/**
		 * 
		 * @return The active tool during this step.
		 */
		public Tool getTool() {
			return mTool;
		}
		/**
		 * @param aLine The actual text of the line of G-Code.
		 * @param aToolLocation Location of every machine axis after performing this move.
		 * @param aTool The active tool during this step.
		 */
		public GCodeLine (final String aLine, final double[] aToolLocation, final Tool aTool) {
			this.mLine = aLine;
			this.mTool = aTool;
			if (aToolLocation != null) {
				this.mToolLocation = Arrays.copyOf(aToolLocation, aToolLocation.length);
			}
		}
		/**
		 * Return the location of every machine axis after performing this move.
		 * @return May be null in header
		 */
		public double[] getToolLocation() {
			return mToolLocation;
		}
		/**
		 * @return The actual text of the line of G-Code.
		 */
		@Override
		public String toString() {
			return mLine;
		}
	}
	/**
	 * Our G-code lines to handle.
	 */
	private List<GCodeLine> mGCodeLines = new ArrayList<GCodeLine>();
	/**
	 * May return null.
	 * @param anIndex line number starting with 0
	 * @return Location of every machine axis after performing the given move.
	 */
	public double[] getToolLocation(int anIndex) {
		GCodeLine line = getElementAt(anIndex);
		if (line == null) {
			return null;
		}
		return line.getToolLocation();
	}
	/**
	 * 
	 * May return null.
	 * @param anIndex line number starting with 0
	 * @return The {@link GCodeLine}.
	 */
	@Override
	public GCodeLine getElementAt(int anIndex) {
		if (anIndex < 0) {
			return null;
		}if (mGCodeLines.size() >= anIndex) {
			return null;
		}
		return mGCodeLines.get(anIndex);
	}
	/**
	 * @return the number of G-Code lines.
	 */
	@Override
	public int getSize() {
		return mGCodeLines.size();
	}
	/**
	 * @param aGCodeLine a line of G-Code to add to the end of the program.
	 */
	public void addElement(final GCodeLine aGCodeLine) {
		this.mGCodeLines.add(aGCodeLine);
		fireIntervalAdded(this, getSize() - 1, getSize());
	}
	/**
	 * Clear the program.
	 */
	public void clear() {
		this.mGCodeLines.clear();
		fireContentsChanged(this, 0, 0);		
	}

}
