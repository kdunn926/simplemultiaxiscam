package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.ChainStrategy;
import biz.wolschon.cam.multiaxis.strategy.FollowSurfaceNormalCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.strategy.LinearStrategy;
import biz.wolschon.cam.multiaxis.strategy.StraightZCutStrategy;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.tools.ToolRepository;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * Panel to enter all tool and strategy information.
 */
public class StrategyCreationPanel extends JPanel {

	/**
	 * For Serializable.
	 */
	private static final long serialVersionUID = 8504617103443377316L;
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
	 * Currently selected tool.
	 */
	private Tool mTool;
	/**
	 * Currently selected strategy and also the JPanel for entering it's parameters.
	 */
	private StrategySelection mStrategyPanel;

	private String mLabel;
	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String aLabel) {
		mLabel = aLabel;
	}

	/**
	 * Abstract interface for a selectable strategy and it's UI.
	 */
	private static abstract class StrategySelection extends JPanel {
		/**
		 * For {@link Serializable}.
		 */
		private static final long serialVersionUID = -189963420158277708L;
		private String mLabel;
		public StrategySelection(final String aLabel) {
			this.mLabel = aLabel;
		}
		/**
		 * @see returns the label.
		 */
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
	private final StrategySelection PARALLEL = new StrategySelection("Parallel") {
		/**
		 * For {@link Serializable}.
		 */
		private static final long serialVersionUID = -7480187587759638513L;
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
			
			// 2. every step of 1., do around the A axis in 10� steps
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
	private final StrategySelection CROSSWISE = new StrategySelection("Crosswise") {
		/**
		 * For {@link Serializable}.
		 */
		private static final long serialVersionUID = -2045718365086921240L;
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
			
			LinearStrategy child0 = new LinearStrategy(aModel, (Axis) firstAxis.getSelectedValue(), Double.parseDouble(firstAxisStep.getText()), aNextStrategy);
			LinearStrategy parent0 = new LinearStrategy(aModel,(Axis) secondAxis.getSelectedValue(), Double.parseDouble(secondAxisStep.getText()), child0);
			child0.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			LinearStrategy child1 = new LinearStrategy(aModel,(Axis) secondAxis.getSelectedValue(), Double.parseDouble(secondAxisStep.getText()), aNextStrategy);
			LinearStrategy parent1 = new LinearStrategy(aModel, (Axis) firstAxis.getSelectedValue(), Double.parseDouble(firstAxisStep.getText()), child1);
			child1.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			return new ChainStrategy(parent0, parent1);
		}
	};
	private JPanel mainPanel;
	/**
	 * Create the panel.
	 */
	private ToolRepository mToolRepository;
	public StrategyCreationPanel (final String aLabel, final ToolRepository aToolRepository) {
		this.mLabel = aLabel;
		this.mToolRepository = aToolRepository;
		setLayout(new BorderLayout());
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(7, 1));
		add(mainPanel, BorderLayout.CENTER);
		
		mParameterSettings = new JTabbedPane(JTabbedPane.TOP);
		add(mParameterSettings, BorderLayout.SOUTH);

		//-------------- tool
		final DefaultListModel model = new DefaultListModel();
		for (Tool tool : mToolRepository.getAllTools()) {
			model.addElement(tool);
		}
		model.addElement("add...");
		mTools = new JList(model);
		mTools.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mTools.addListSelectionListener(new ListSelectionListener() {
			private boolean disabled = false;
			@Override
			public void valueChanged(ListSelectionEvent anEvent) {
				Object selectedValue = mTools.getSelectedValue();
				if (!(selectedValue instanceof Tool)) {
					if (disabled || selectedValue == null || anEvent.getValueIsAdjusting()) {
						return;
					}
					final JDialog dlg = new JDialog();
					dlg.setTitle("" + System.currentTimeMillis());
					final ToolCreationPanel toolCreationPanel = new ToolCreationPanel();
					toolCreationPanel.getSaveButton().addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent aArg0) {
							disabled = true;
							try {
								Tool createdTool = toolCreationPanel.getTool();
								dlg.setVisible(false);
								dlg.dispose();
								model.insertElementAt(createdTool, model.size() - 1);// insert before "add..."
								mTools.setSelectedValue(createdTool, true);
								mToolRepository.addTool(createdTool, true);
							} finally {
								disabled = false;
							}
						}
					});
					dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dlg.setContentPane(toolCreationPanel);
					dlg.pack();//TODO: listner
					dlg.setVisible(true);
					return;
				}
				Tool tool = (Tool) selectedValue;
				StrategyCreationPanel.this.mTool = tool;
				
			}
		});
		mainPanel.add(new JScrollPane(mTools), null);
		mTools.setSelectedIndex(0);
		mTools.setVisibleRowCount(1);

		mainPanel.add(new JLabel("[---] tool RPM"), null);
		mainPanel.add(new JLabel("[use model size] segment"), null);

		//-------------- strategy
		//TODO: add waterline strategy
		mStrategies = new JList(new StrategySelection[] {PARALLEL, CROSSWISE});
		mStrategies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mStrategies.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				StrategySelection s = (StrategySelection) mStrategies.getSelectedValue();
				onStrategyChanged(s);
				
			}
		});
		mainPanel.add(new JScrollPane(mStrategies), null);
		mStrategies.setSelectedValue(PARALLEL, true);
		mStrategies.setVisibleRowCount(1);
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
	 * The user has selected a new strategy, show it's UI, so the user can select parameters.
	 */
	private void onStrategyChanged(final StrategySelection aStrategySelection) {
		if (mStrategyPanel != null) {
			mParameterSettings.remove(mStrategyPanel);
			mStrategyPanel = null;
		}
		//TODO: clean this up
		mStrategyPanel = (StrategySelection) aStrategySelection.getPanel();
		mParameterSettings.addTab("strategy settings", mStrategyPanel);
		invalidate();
	}

	public Tool getTool() {
		return mTool;
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

	@Override
	public String toString() {
		return mLabel;
	}
}
