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
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.FollowSurfaceNormalCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.strategy.LinearStrategy;
import biz.wolschon.cam.multiaxis.strategy.StraightZCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.ChainStrategy;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;
import biz.wolschon.cam.multiaxis.tools.BallShape;
import biz.wolschon.cam.multiaxis.tools.Tool;

/**
 * Panel to enter all tool and strategy information.
 */
public class StrategyCreationPanel extends JPanel {

	/**
	 * Parent for the parameter settings for tools and strategy.
	 */
	private JTabbedPane mParameterSettings;
	/**
	 * Checkbox to add a strategy to follow the surface normal on the A axis to the chain of command.
	 */
	private JCheckBox mReal4Axis;

	/**
	 * Checkbox to add a strategy to follow the surface normal on the B axis to the chain of command.
	 */
	private JCheckBox mReal5Axis;
	/**
	 * List to select a tool from.<br/>
	 * Uppon selecting the tool #onToolSelected() takes care of displaying the UI for it's parameters
	 */
	private JList mTools;
	/**
	 * List to select a strategy from.<br/>
	 * Uppon selecting the strategy #onStrategySelected() takes care of displaying the UI for it's parameters
	 */
	private JList mStrategies;
	/**
	 * Currently selected tool and also the JPanel for entering it's parameters.
	 */
	private ToolSelection mToolPanel;
	/**
	 * Currently selected strategy and also the JPanel for entering it's parameters.
	 */
	private StrategySelection mStrategyPanel;

	/**
	 * Abstract interface for a selectable strategy and it's UI.
	 */
	private static abstract class StrategySelection extends JPanel {
		private String mLabel;
		public StrategySelection(final String aLabel) {
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
		 * Create the strategy according to the user entered parameters.
		 */
		public abstract IStrategy getStrategy(final IModel aModel, final IStrategy aNextStrategy);
	};

	/**
	 * A parallel strategy moving along 2 axis.
	 */
	private static final StrategySelection PARALLEL = new StrategySelection("Parallel") {		private JTextField diameter = new JTextField("1.0");
		private JList firstAxis 	= new JList(Axis.values());
		private JTextField firstAxisStep = new JTextField("0.33");
		private JList secondAxis = new JList(Axis.values());
		private JTextField secondAxisStep = new JTextField("1");
		private JList cuttingDirection = new JList(LinearStrategy.Direction.values());
		private boolean initialized = false;
		@Override
		public JPanel getPanel() {
			if (initialized) {
				return this;
			}
			initialized = true;
			JPanel panel = this;
			panel.setLayout(new GridLayout(5, 2));

			panel.add(new JLabel("first axis:"), null);
			panel.add(new JScrollPane(firstAxis), null);
			panel.add(new JLabel("path distance:"), null);
			panel.add(firstAxisStep, null);


			panel.add(new JLabel("second axis:"), null);
			panel.add(new JScrollPane(secondAxis), null);
			panel.add(new JLabel("path distance:"), null);
			panel.add(secondAxisStep, null);

			panel.add(new JLabel("cutting direction"), null);
			panel.add(new JScrollPane(cuttingDirection));

			firstAxis.setSelectedValue(Axis.X, true);
			firstAxis.setVisibleRowCount(1);
			secondAxis.setSelectedValue(Axis.A, true);
			secondAxis.setVisibleRowCount(1);
			cuttingDirection.setSelectedValue(LinearStrategy.Direction.Meander, true);
			cuttingDirection.setVisibleRowCount(1);
			return panel;
		}
		public IStrategy getStrategy(final IModel aModel, final IStrategy aNextStrategy) {
			
			// 2. every step of 1., do around the A axis in 10° steps
			LinearStrategy aroundAAxis = new LinearStrategy(aModel, (Axis) firstAxis.getSelectedValue(), Double.parseDouble(firstAxisStep.getText()), aNextStrategy);
			aroundAAxis.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			// 1. start by moving along the Y axis in 1.1mm steps
			LinearStrategy alongXAxis = new LinearStrategy(aModel,(Axis) secondAxis.getSelectedValue(), Double.parseDouble(secondAxisStep.getText()), aroundAAxis);

			return alongXAxis;
		}
	};
	/**
	 * A crosswise strategy moving along the same 2 axis twice in different order.
	 */
	private static final StrategySelection CROSSWISE = new StrategySelection("Crosswise") {
		private JTextField diameter = new JTextField("1.0");
		private JList firstAxis 	= new JList(Axis.values());
		private JTextField firstAxisStep = new JTextField("0.33");
		private JList secondAxis = new JList(Axis.values());
		private JTextField secondAxisStep = new JTextField("1");
		private JList cuttingDirection = new JList(LinearStrategy.Direction.values());
		private boolean initialized = false;
		@Override
		public JPanel getPanel() {
			if (initialized) {
				return this;
			}
			initialized = true;
			JPanel panel = this;
			panel.setLayout(new GridLayout(5, 2));

			panel.add(new JLabel("first axis:"), null);
			panel.add(new JScrollPane(firstAxis), null);
			panel.add(new JLabel("path distance:"), null);
			panel.add(firstAxisStep, null);


			panel.add(new JLabel("second axis:"), null);
			panel.add(new JScrollPane(secondAxis), null);
			panel.add(new JLabel("path distance:"), null);
			panel.add(secondAxisStep, null);

			panel.add(new JLabel("cutting direction"), null);
			panel.add(new JScrollPane(cuttingDirection));

			firstAxis.setSelectedValue(Axis.X, true);
			secondAxis.setSelectedValue(Axis.A, true);
			firstAxis.setVisibleRowCount(1);
			secondAxis.setVisibleRowCount(1);
			cuttingDirection.setSelectedValue(LinearStrategy.Direction.Meander, true);
			cuttingDirection.setVisibleRowCount(1);
			return panel;
		}
		public IStrategy getStrategy(final IModel aModel, final IStrategy aNextStrategy) {
			
			LinearStrategy aroundAAxis0 = new LinearStrategy(aModel, (Axis) firstAxis.getSelectedValue(), Double.parseDouble(firstAxisStep.getText()), aNextStrategy);
			LinearStrategy alongXAxis0 = new LinearStrategy(aModel,(Axis) secondAxis.getSelectedValue(), Double.parseDouble(secondAxisStep.getText()), aroundAAxis0);
			aroundAAxis0.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			LinearStrategy alongXAxis1 = new LinearStrategy(aModel,(Axis) secondAxis.getSelectedValue(), Double.parseDouble(secondAxisStep.getText()), aNextStrategy);
			LinearStrategy aroundAAxis1 = new LinearStrategy(aModel, (Axis) firstAxis.getSelectedValue(), Double.parseDouble(firstAxisStep.getText()), alongXAxis1);
			alongXAxis1.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			return new ChainStrategy(alongXAxis0, aroundAAxis1);
		}
	};
	/**
	 * Abstract interface for a selectable tool and it's UI.
	 */
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
		private boolean initialized = false;
		@Override
		public JPanel getPanel() {
			if (initialized) {
				return this;
			}
			initialized = true;
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
		private boolean initialized = false;
		@Override
		public JPanel getPanel() {
			if (initialized) {
				return this;
			}
			initialized = true;
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
		mainPanel.setLayout(new GridLayout(7, 1));
		add(mainPanel, BorderLayout.CENTER);
		
		mParameterSettings = new JTabbedPane(JTabbedPane.TOP);
		add(mParameterSettings, BorderLayout.SOUTH);

		//-------------- tool
		mTools = new JList(new ToolSelection[] {TOOL0, TOOL1});
		mTools.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				ToolSelection tool = (ToolSelection) mTools.getSelectedValue();
				onToolChanged(tool);
				
			}
		});
		mainPanel.add(mTools, null);
		mTools.setSelectedValue(TOOL0, true);
		onToolChanged(TOOL0);

		mainPanel.add(new JLabel("[---] tool RPM"), null);
		mainPanel.add(new JLabel("[use model size] segment"), null);

		//-------------- strategy
		//TODO: add waterline strategy
		mStrategies = new JList(new StrategySelection[] {PARALLEL, CROSSWISE});
		mStrategies.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				StrategySelection s = (StrategySelection) mStrategies.getSelectedValue();
				onStrategyChanged(s);
				
			}
		});
		mainPanel.add(mStrategies, null);
		mStrategies.setSelectedValue(PARALLEL, true);
		onStrategyChanged(PARALLEL);

		mainPanel.add(new JLabel("[finishing step, no layers, 0mm skin] layers"), null);

		mReal4Axis = new JCheckBox("follow A axis surface normal using Y");
		mReal4Axis.setSelected(true);
		mainPanel.add(mReal4Axis, null);
		
		mReal5Axis = new JCheckBox("follow B axis surface normal using X");
		mReal5Axis.setSelected(false);
		mReal5Axis.setEnabled(false);
		mainPanel.add(mReal5Axis, null);
		
	}

	/**
	 * The user has selected a new tool, show it's UI, so the user can select parameters.
	 */
	private void onToolChanged(final ToolSelection aToolSelection) {
		if (mToolPanel != null) {
			mParameterSettings.remove(mToolPanel);
			mToolPanel = null;
		}
		//TODO: clean this up
		mToolPanel = (ToolSelection) aToolSelection.getPanel();
		mParameterSettings.addTab("tool settings", mToolPanel);
		invalidate();
	}

	/**
	 * The user has selected a new strategy, show it's UI, so the user can select parameters.
	 */
	private void onStrategyChanged(final StrategySelection aStrategySelection) {
		if (mStrategyPanel != null) {
			mParameterSettings.remove(mToolPanel);
			mStrategyPanel = null;
		}
		//TODO: clean this up
		mStrategyPanel = (StrategySelection) aStrategySelection.getPanel();
		mParameterSettings.addTab("strategy settings", mStrategyPanel);
		invalidate();
	}

	public Tool getTool() {
		return mToolPanel.getTool();
	}
	/**
	 * Create the strategy chain of command to execute on the model.
	 */
	public IStrategy getStrategy(final GCodeWriterStrategy anOutputStrategy, final IModel aModel) {
		IStrategy cutStrategy = anOutputStrategy;
		if (mReal5Axis.isSelected()) {
			// 4. do real 5 axis cutting into real 4 axis cutting
			cutStrategy = new FollowSurfaceNormalCutStrategy(aModel, Axis.B, cutStrategy, getTool());
		}
		if (mReal4Axis.isSelected()) {
			// 3. do real 4 axis cutting into real 4 axis cutting
			cutStrategy = new FollowSurfaceNormalCutStrategy(aModel, Axis.A, cutStrategy, getTool());
		} else {
			// 3. do fake 4 axis cutting by cutting only straight down
			cutStrategy = new StraightZCutStrategy(aModel, cutStrategy, getTool());
		}

		// 1. and 2. move along first and second axis
		return mStrategyPanel.getStrategy(aModel, cutStrategy );
	}

}
