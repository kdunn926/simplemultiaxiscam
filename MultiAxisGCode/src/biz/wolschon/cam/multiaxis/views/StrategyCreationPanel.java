package biz.wolschon.cam.multiaxis.views;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JList;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.FollowSurfaceNormalCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.strategy.LinearStrategy;
import biz.wolschon.cam.multiaxis.strategy.StraightZCutStrategy;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;
import biz.wolschon.cam.multiaxis.tools.BallShape;
import biz.wolschon.cam.multiaxis.tools.Tool;

/**
 * Panel to enter all tool and strategy information.
 */
public class StrategyCreationPanel extends JPanel {


	/**
	 * The tool we use Currently fixed to a 1.0mm ball nose cutter.
	 */
	private Tool mTool = new Tool(1.0d);

	private JCheckBox mReal4Axis;

	/**
	 * Create the panel.
	 */
	public StrategyCreationPanel () {
		setLayout(new GridLayout(5, 1));
		
		//TODO: allow more strategies. Replace dummy JLabels with actual inputs.
		add(new JLabel("[6mm Ball nose] tool"), null);
		add(new JLabel("[use model size] segment"), null);
		add(new JLabel("[parallel cutting A, then Y Axis] strategy"), null);
		add(new JLabel("[finishing step, no layers, 0mm skin] layers"), null);

		mReal4Axis = new JCheckBox("follow A axis surface normal");
		mReal4Axis.setSelected(true);
		add(mReal4Axis, null);
		
	}

	public Tool getTool() {
		return mTool;
	}
	public IStrategy getStrategy(GCodeWriterStrategy out, final IModel aModel) {

				//TODO: use the Tool
				//TODO: let the user choose the strategy
				IStrategy cutStrategy = null;
				if (mReal4Axis.isSelected()) {
					// 3. do real 4 axis cutting into real 4 axis cutting
					cutStrategy = new FollowSurfaceNormalCutStrategy(aModel, Axis.A, out, mTool);
				} else {
					// 3. do fake 4 axis cutting by cutting only straight down
					cutStrategy = new StraightZCutStrategy(aModel, cutStrategy);
				}

				// 2. every step of 1., do around the A axis in 10° steps
				IStrategy aroundAAxis = new LinearStrategy(aModel, Axis.A, 1.0d, cutStrategy );

				// 1. start by moving along the Y axis in 1.1mm steps
				LinearStrategy alongXAxis = new LinearStrategy(aModel, Axis.X, 0.33d, aroundAAxis);
				
			return alongXAxis;
	}

}
