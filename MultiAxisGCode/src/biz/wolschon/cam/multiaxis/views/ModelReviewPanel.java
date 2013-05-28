package biz.wolschon.cam.multiaxis.views;

import javax.swing.JPanel;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

import java.awt.GridLayout;
import java.io.Serializable;

public class ModelReviewPanel extends JPanel {

	/**
	 * For {@link Serializable}.
	 */
	private static final long serialVersionUID = -2144909213978050312L;
	private ParallelProjectionView panelXZ;
	private ParallelProjectionView panelYZ;
	private ParallelProjectionView panelXY;

	/**
	 * Create the panel.
	 */
	public ModelReviewPanel(final IModel model) {
		setLayout(new GridLayout(2, 2));
		
		panelXZ = new ParallelProjectionView(model, null, "front");
		add(panelXZ);
		
		panelYZ = new ParallelProjectionView(model, null, "right");
		panelYZ.setHorizontalAxis(Axis.Y);
		add(panelYZ);

		panelXY = new ParallelProjectionView(model, null, "top");
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
