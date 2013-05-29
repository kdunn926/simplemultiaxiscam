package biz.wolschon.cam.multiaxis.views;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

	private JCheckBox mShowPath = new JCheckBox("show toolpath");
	private JCheckBox mShowTool = new JCheckBox("show tool");
	private JCheckBox mShowGeometry = new JCheckBox("show geometry");
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
		panel_3.setLayout(new GridLayout(3, 1));
		panel_3.add(mShowGeometry);
		panel_3.add(mShowPath);
		panel_3.add(mShowTool);
		add(panel_3);

		mShowPath.setSelected(panelXY.isShowPath());
		mShowPath.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent aArg0) {
				panelXZ.setShowPath(mShowPath.isSelected());
				panelYZ.setShowPath(mShowPath.isSelected());
				panelXY.setShowPath(mShowPath.isSelected());
			}
		});
		mShowGeometry.setSelected(panelXY.isShowModel());
		mShowGeometry.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent aArg0) {
				panelXZ.setShowModel(mShowGeometry.isSelected());
				panelYZ.setShowModel(mShowGeometry.isSelected());
				panelXY.setShowModel(mShowGeometry.isSelected());
			}
		});
		mShowTool.setSelected(panelXY.isShowTool());
		mShowTool.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent aArg0) {
				panelXZ.setShowTool(mShowTool.isSelected());
				panelYZ.setShowTool(mShowTool.isSelected());
				panelXY.setShowTool(mShowTool.isSelected());
			}
		});
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
	/**
	 * @param aGCodeModel the gCodeModel to set
	 */
	public void setGCodeModel(final GCodeModel aGCodeModel) {
		panelXZ.setGCodeModel(aGCodeModel);
		panelYZ.setGCodeModel(aGCodeModel);
		panelXY.setGCodeModel(aGCodeModel);
	}
}
