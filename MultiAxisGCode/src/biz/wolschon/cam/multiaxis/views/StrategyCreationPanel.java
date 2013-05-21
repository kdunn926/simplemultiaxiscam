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
import javax.swing.JTextField;

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


	private JCheckBox mReal4Axis;
	private JList mTools;
	private ToolSelection mToolPanel;

	private static abstract class ToolSelection extends JPanel {
		private String mLabel;
		public ToolSelection(final String aLabel) {
			this.mLabel = aLabel;
		}
		@Override
		public String toString() {
			return mLabel;
		}
		/**
		 * Tool specific panel to enter the parameters.
		 */
		public abstract JPanel getPanel();
		/**
		 * Create the tool according to the user entered parameters.
		 */
		public abstract Tool getTool();
	}
	/**
	 * Our default tool (a ball shaped cutter).
	 */
	private static final ToolSelection TOOL0 = new ToolSelection("Ball Nose") {
		private JTextField diameter = new JTextField("1.0");
		private JTextField length 	= new JTextField("10.0");
		private JTextField shaftDiameter = new JTextField("0.5");
		@Override
		public JPanel getPanel() {
			JPanel panel = this;
			panel.setLayout(new GridLayout(3, 1));

			panel.add(new JLabel("ball diameter:"), null);
			panel.add(diameter, null);

			panel.add(new JLabel("full length:"), null);
			panel.add(length, null);

			panel.add(new JLabel("shaft diameter:"), null);
			panel.add(shaftDiameter, null);

			return panel;
		}
		public Tool getTool() {
			return Tool.createBallCutter(Double.parseDouble(diameter.getText()),
					Double.parseDouble(length.getText()),
					Double.parseDouble(shaftDiameter.getText()));
		}
	};

	/**
	 * A flat cutter tool.
	 */
	private static final ToolSelection TOOL1 = new ToolSelection("Flat Cutter") {
		private JTextField diameter = new JTextField("1.0");
		private JTextField length = new JTextField("10.0");
		private JTextField cutterLength = new JTextField("5.0");
		private JTextField shaftDiameter = new JTextField("0.5");
		@Override
		public JPanel getPanel() {
			JPanel panel = this;
			panel.setLayout(new GridLayout(4, 2));

			panel.add(new JLabel("diameter:"), null);
			panel.add(diameter, null);

			panel.add(new JLabel("cutter  length:"), null);
			panel.add(cutterLength, null);

			panel.add(new JLabel("full length:"), null);
			panel.add(length, null);

			panel.add(new JLabel("shaft diameter:"), null);
			panel.add(shaftDiameter, null);

			return panel;
		}
		public Tool getTool() {
			return Tool.createFlatCutter(Double.parseDouble(diameter.getText()),
					Double.parseDouble(cutterLength.getText()),
					Double.parseDouble(length.getText()),
					Double.parseDouble(shaftDiameter.getText()));
		}
	};
	private JPanel mainPanel;
	/**
	 * Create the panel.
	 */
	public StrategyCreationPanel () {
		setLayout(new BorderLayout());
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(6, 1));
		add(mainPanel, BorderLayout.CENTER);
		
		//TODO: allow more strategies. Replace dummy JLabels with actual inputs.
		mTools = new JList(new ToolSelection[] {TOOL0, TOOL1});
		mTools.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				ToolSelection tool = (ToolSelection) mTools.getSelectedValue();
				onToolChanged(tool);
				
			}
		});
		mainPanel.add(mTools, null);
		onToolChanged(TOOL0);

		mainPanel.add(new JLabel("[---] tool RPM"), null);
		mainPanel.add(new JLabel("[use model size] segment"), null);
		mainPanel.add(new JLabel("[parallel cutting A, then Y Axis] strategy"), null);
		mainPanel.add(new JLabel("[finishing step, no layers, 0mm skin] layers"), null);

		mReal4Axis = new JCheckBox("follow A axis surface normal");
		mReal4Axis.setSelected(true);
		mainPanel.add(mReal4Axis, null);
		
	}

	private void onToolChanged(final ToolSelection aToolSelection) {
		if (mToolPanel != null) {
			remove(mToolPanel);
			mToolPanel = null;
		}
		//TODO: clean this up
		mToolPanel = (ToolSelection) aToolSelection.getPanel();
		add(mToolPanel, BorderLayout.SOUTH);
		invalidate();
	}

	public Tool getTool() {
		return mToolPanel.getTool();
	}
	public IStrategy getStrategy(GCodeWriterStrategy out, final IModel aModel) {

				//TODO: use the Tool
				//TODO: let the user choose the strategy
				IStrategy cutStrategy = null;
				if (mReal4Axis.isSelected()) {
					// 3. do real 4 axis cutting into real 4 axis cutting
					cutStrategy = new FollowSurfaceNormalCutStrategy(aModel, Axis.A, out, getTool());
				} else {
					// 3. do fake 4 axis cutting by cutting only straight down
					cutStrategy = new StraightZCutStrategy(aModel, cutStrategy, getTool());
				}

				// 2. every step of 1., do around the A axis in 10° steps
				IStrategy aroundAAxis = new LinearStrategy(aModel, Axis.A, 1.0d, cutStrategy );

				// 1. start by moving along the Y axis in 1.1mm steps
				LinearStrategy alongXAxis = new LinearStrategy(aModel, Axis.X, 0.33d, aroundAAxis);
				
			return alongXAxis;
	}

}
