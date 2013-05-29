package biz.wolschon.cam.multiaxis.views;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import biz.wolschon.cam.multiaxis.tools.Tool;

public class GCodeModel extends DefaultListModel {

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
	public double[] getToolLocation(int anIndex) {
		GCodeLine line = (GCodeLine) get(anIndex);
		return line.getToolLocation();
	}

}
