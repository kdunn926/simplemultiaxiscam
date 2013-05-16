package biz.wolschon.cam.multiaxis.views;

import javax.swing.JPanel;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

import java.awt.GridLayout;

public class ModelReviewPanel extends JPanel {

	private ParallelProjectionView panelXZ;
	private ParallelProjectionView panelYZ;
	private ParallelProjectionView panelXY;

	/**
	 * Create the panel.
	 */
	public ModelReviewPanel(final IModel model) {
		setLayout(new GridLayout(2, 2));
		
		panelXZ = new ParallelProjectionView(model, null);
		add(panelXZ);
		
		panelYZ = new ParallelProjectionView(model, null);
		panelYZ.setHorizontalAxis(Axis.Y);
		add(panelYZ);

		panelXY = new ParallelProjectionView(model, null);
		panelXY.setVerticalAxis(Axis.X);
		panelXY.setHorizontalAxis(Axis.Y);
		add(panelXY);
		
		JPanel panel_3 = new JPanel();
		add(panel_3);
	}

	public void setToolLocation(final double[] aToolLocation) {
		panelXZ.setToolLocation(aToolLocation);
		panelYZ.setToolLocation(aToolLocation);
		panelXY.setToolLocation(aToolLocation);
	}
	public void setTool(final Tool aTool) {
		panelXZ.setTool(aTool);
		panelYZ.setTool(aTool);
		panelXY.setTool(aTool);
	}	
}
