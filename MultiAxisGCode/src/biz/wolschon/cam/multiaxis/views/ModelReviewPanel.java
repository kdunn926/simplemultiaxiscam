package biz.wolschon.cam.multiaxis.views;

import javax.swing.JPanel;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

import java.awt.GridLayout;

public class ModelReviewPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public ModelReviewPanel(final IModel model) {
		setLayout(new GridLayout(2, 2));
		
		ParallelProjectionView panelXZ = new ParallelProjectionView(model, null);
		add(panelXZ);
		
		ParallelProjectionView panelYZ = new ParallelProjectionView(model, null);
		panelYZ.setHorizontalAxis(Axis.Y);
		add(panelYZ);

		ParallelProjectionView panelXY = new ParallelProjectionView(model, null);
		panelXY.setVerticalAxis(Axis.X);
		panelXY.setHorizontalAxis(Axis.Y);
		add(panelXY);
		
		JPanel panel_3 = new JPanel();
		add(panel_3);
	}

}
