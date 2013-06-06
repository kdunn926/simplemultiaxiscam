package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.ChainStrategy;
import biz.wolschon.cam.multiaxis.strategy.FollowSurfaceNormalCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.strategy.LayerStrategy;
import biz.wolschon.cam.multiaxis.strategy.LinearStrategy;
import biz.wolschon.cam.multiaxis.strategy.StraightZCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.ZLimitingStrategy;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.tools.ToolRepository;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * Panel to enter all tool and strategy information.
 */
public class StrategyCreationPanel extends JPanel implements ISegmentSelectionListener {

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
	private JSpinner freeMovementHeight = new JSpinner();
	private JSpinner feedRate = new JSpinner();
	/**
	 * List to select a tool from.<br/>
	 * Uppon selecting the tool #onToolSelected() takes care of displaying the UI for it's parameters
	 */
	private JList mTools;
	private JPanel mToolTab;
	/**
	 * List to select a strategy from.<br/>
	 * Uppon selecting the strategy #onStrategySelected() takes care of displaying the UI for it's parameters
	 */
	private JList mStrategies;

	/**
	 * Limit the range of the current strategy.
	 */
	private Limit mSegment;
	/**
	 * Currently selected tool.
	 */
	private Tool mTool;
	private IModel mModel;
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
		/**
		 * @return The list of axes that can be part of a Segment {@link Limit}.
		 */
		public abstract Collection<Axis> getLimitableAxes();
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
			Axis firstAxisValue = (Axis) firstAxis.getSelectedValue();
			LinearStrategy aroundAAxis = new LinearStrategy(aModel, firstAxisValue, Double.parseDouble(firstAxisStep.getText()), aNextStrategy);
			if (mSegment != null && mSegment.isAxisLimited(firstAxisValue)) {
				aroundAAxis.setMinLimit(mSegment.getMinimum(firstAxisValue));
				aroundAAxis.setMaxLimit(mSegment.getMaximum(firstAxisValue));
			}
			aroundAAxis.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			// 1. start by moving along the Y axis in 1.1mm steps
			Axis secondAxisValue = (Axis) secondAxis.getSelectedValue();
			LinearStrategy alongXAxis = new LinearStrategy(aModel,secondAxisValue, Double.parseDouble(secondAxisStep.getText()), aroundAAxis);
			if (mSegment != null && mSegment.isAxisLimited(secondAxisValue)) {
				alongXAxis.setMinLimit(mSegment.getMinimum(secondAxisValue));
				alongXAxis.setMaxLimit(mSegment.getMaximum(secondAxisValue));
			}
			return alongXAxis;
		}
		/**
		 * @return The list of axes that can be part of a Segment {@link Limit}.
		 */
		public Collection<Axis> getLimitableAxes() {
			Collection<Axis> retval = new Vector<Axis>();
			final Axis firstAxisValue = (Axis) firstAxis.getSelectedValue();
			final Axis secondAxisValue = (Axis) secondAxis.getSelectedValue();
			retval.add(firstAxisValue);
			retval.add(secondAxisValue);
			return retval;
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
			 
			panel.add(freeMovementHeight);

			firstAxis.setSelectedValue(Axis.X, true);
			secondAxis.setSelectedValue(Axis.A, true);
			firstAxis.setVisibleRowCount(1);
			secondAxis.setVisibleRowCount(1);
			cuttingDirection.setSelectedValue(LinearStrategy.Direction.Meander, true);
			cuttingDirection.setVisibleRowCount(1);
			return panel;
		}
		public IStrategy getStrategy(final IModel aModel, final IStrategy aNextStrategy) {
			
			final Axis firstAxisValue = (Axis) firstAxis.getSelectedValue();
			final Axis secondAxisValue = (Axis) secondAxis.getSelectedValue();
			LinearStrategy child0 = new LinearStrategy(aModel, firstAxisValue, Double.parseDouble(firstAxisStep.getText()), aNextStrategy);
			LinearStrategy parent0 = new LinearStrategy(aModel,secondAxisValue, Double.parseDouble(secondAxisStep.getText()), child0);
			child0.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			LinearStrategy child1 = new LinearStrategy(aModel,secondAxisValue, Double.parseDouble(secondAxisStep.getText()), aNextStrategy);
			LinearStrategy parent1 = new LinearStrategy(aModel, firstAxisValue, Double.parseDouble(firstAxisStep.getText()), child1);
			child1.setDirection((LinearStrategy.Direction) cuttingDirection.getSelectedValue());

			if (mSegment != null && mSegment.isAxisLimited(firstAxisValue)) {
				child0.setMinLimit(mSegment.getMinimum(firstAxisValue));
				child0.setMaxLimit(mSegment.getMaximum(firstAxisValue));
				parent1.setMinLimit(mSegment.getMinimum(firstAxisValue));
				parent1.setMaxLimit(mSegment.getMaximum(firstAxisValue));
			}
			if (mSegment != null && mSegment.isAxisLimited(secondAxisValue)) {
				parent0.setMinLimit(mSegment.getMinimum(secondAxisValue));
				parent0.setMaxLimit(mSegment.getMaximum(secondAxisValue));
				child1.setMinLimit(mSegment.getMinimum(secondAxisValue));
				child1.setMaxLimit(mSegment.getMaximum(secondAxisValue));
			}
			
			return new ChainStrategy(parent0, parent1);
		}
		/**
		 * @return The list of axes that can be part of a Segment {@link Limit}.
		 */
		public Collection<Axis> getLimitableAxes() {
			Collection<Axis> retval = new Vector<Axis>();
			final Axis firstAxisValue = (Axis) firstAxis.getSelectedValue();
			final Axis secondAxisValue = (Axis) secondAxis.getSelectedValue();
			retval.add(firstAxisValue);
			retval.add(secondAxisValue);
			return retval;
		}
	};
	private JTabbedPane mainPanel;
	/**
	 * Create the panel.
	 */
	private ToolRepository mToolRepository;
	/**
	 * Panel with the widgets to select a segment.
	 * @see #mSegment.
	 */
	private JPanel mSegmentPanel;
	/**
	 * Used to implement selection of segments.
	 */
	private ModelReviewPanel mReviewTab;
	private JPanel mMovementTab;
	private JPanel mStrategyModificationTab;
	private JCheckBox mRoughing;
	private JSpinner mLayerHeight;
	private JSpinner mSkinThickness;
	public StrategyCreationPanel (final String aLabel, final ToolRepository aToolRepository, final IModel aModel, final ModelReviewPanel aReviewTab) {
		this.mLabel = aLabel;
		this.mModel = aModel;
		this.mReviewTab = aReviewTab;
		this.mReviewTab.setSegment(mSegment);
		this.mToolRepository = aToolRepository;
		setLayout(new BorderLayout());
		mainPanel = new JTabbedPane();
		add(mainPanel, BorderLayout.CENTER);


		//-------------- tool
		mainPanel.addTab("Tool", new JScrollPane(getToolTab()));
		mainPanel.addTab("Segment", getSegmentPanel());		
		mainPanel.addTab("movement", getMovementTab());
		mainPanel.addTab("basic strategy", new JScrollPane(getStrategySelectionTab()));
		mainPanel.addTab("strategy modification", new JScrollPane(getStrategyModificationTab()));
		onStrategyChanged(PARALLEL); // adds the "strategy settings" tab
	}

	private Component getStrategySelectionTab() {
		if (mStrategies == null) {
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
			mStrategies.setSelectedValue(PARALLEL, true);
			mStrategies.setVisibleRowCount(1);
		}
		return mStrategies;
	}

	private Component getStrategyModificationTab() {
		if (mStrategyModificationTab == null) {
			mStrategyModificationTab = new JPanel();
			mStrategyModificationTab.setLayout(new GridLayout(5, 1));

			mRoughing = new JCheckBox("roughing");
			mRoughing.setSelected(false);
			mStrategyModificationTab.add(mRoughing, null);
			mRoughing.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent aArg0) {
					mLayerHeight.setEnabled(mRoughing.isSelected());
					//TODO: net yet implemented mSkinThickness.setEnabled(mRoughing.isSelected());
				}
			});
	
			mLayerHeight = new JSpinner();
			{
				JPanel temp = new JPanel(new BorderLayout());
				temp.add(new JLabel("layer height"), BorderLayout.EAST);
				temp.add(mLayerHeight, BorderLayout.CENTER);
				SpinnerNumberModel snmodel = new SpinnerNumberModel(1.0d, 0.0001d, mModel.getMaxZ() - mModel.getMinZ(), 0.05d); 
				mLayerHeight.setModel(snmodel);
				mLayerHeight.setEnabled(false);
				mStrategyModificationTab.add(temp, null);
			}
	
			mSkinThickness = new JSpinner();
			{
				JPanel temp = new JPanel(new BorderLayout());
				temp.add(new JLabel("skin thickness"), BorderLayout.EAST);
				temp.add(mSkinThickness, BorderLayout.CENTER);
				SpinnerNumberModel snmodel = new SpinnerNumberModel(0.4d, 0.0001, mModel.getMaxZ() - mModel.getMinZ(), 0.05); 
				mSkinThickness.setModel(snmodel);
				mSkinThickness.setEnabled(false);
				mStrategyModificationTab.add(temp, null);
			}

			mReal4Axis = new JCheckBox("follow A axis surface normal using Y");
			mReal4Axis.setSelected(true);
			mStrategyModificationTab.add(mReal4Axis, null);
			
			mReal5Axis = new JCheckBox("follow B axis surface normal using X");
			mReal5Axis.setSelected(false);
			mReal5Axis.setEnabled(false);  // NOT YET IMPLEMENTED
			mStrategyModificationTab.add(mReal5Axis, null);
		}
		return mStrategyModificationTab;
	}

	private Component getMovementTab() {
		if (mMovementTab == null) {
			mMovementTab = new JPanel();
			mMovementTab.setLayout(new GridLayout(2, 1));

			JPanel temp = new JPanel(new BorderLayout());
			temp.add(new JLabel("free movement heigth"), BorderLayout.EAST);
			temp.add(freeMovementHeight, BorderLayout.WEST);
			SpinnerNumberModel snmodel = new SpinnerNumberModel(mModel.getMaxZ() + 5, mModel.getMinZ() - 100, mModel.getMaxZ() + 100, 1); 
			freeMovementHeight.setModel(snmodel);
			
			mMovementTab.add(temp);

			JPanel temp2 = new JPanel(new BorderLayout());
			temp2.add(new JLabel("feed rate(mm/min)"), BorderLayout.EAST);
			temp2.add(feedRate, BorderLayout.WEST);
			SpinnerNumberModel frmodel = new SpinnerNumberModel(500, 100, 10000, 10); 
			feedRate.setModel(frmodel);
			
			mMovementTab.add(temp2);
		}
		return mMovementTab;
	}

	private Component getToolTab() {
		if (mToolTab != null) {
			return mToolTab;
		}
		JPanel mToolTab = new JPanel();
		mToolTab.setLayout(new BorderLayout());
		mToolTab.add(new JLabel("[---] tool RPM"), BorderLayout.SOUTH);

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
					dlg.pack();
					dlg.setVisible(true);
					return;
				}
				Tool tool = (Tool) selectedValue;
				StrategyCreationPanel.this.mTool = tool;
				
			}
		});
		mTools.setSelectedIndex(0);
		mTools.setVisibleRowCount(1);

		mToolTab.add(mTools, BorderLayout.CENTER);
		return mToolTab;
	}

	/**
	 * @return
	 */
	protected JComponent getSegmentPanel() {
		if (this.mSegmentPanel == null) {
			this.mSegmentPanel = new JPanel();
			this.mSegmentPanel.setLayout(new BorderLayout(0, 0));
			final JCheckBox useSegmentCheckbox = new JCheckBox("limit to segment");
			this.mSegmentPanel.add(useSegmentCheckbox, BorderLayout.WEST);
			final JButton segmentSelectionButton = new JButton("select");
			this.mSegmentPanel.add(segmentSelectionButton, BorderLayout.EAST);

			segmentSelectionButton.setEnabled(useSegmentCheckbox.isSelected());
			segmentSelectionButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent aE) {
					onSelectSegment();
					
				}
			});
			useSegmentCheckbox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent aArg0) {
					segmentSelectionButton.setEnabled(useSegmentCheckbox.isSelected());
					if (StrategyCreationPanel.this.mSegment == null) {
						onSelectSegment();
					}
				}
			});
		}
		return this.mSegmentPanel;
	}

	protected void onSelectSegment() {
		//collect the list of axis actually used to limit the selection to these axis
		Collection<Axis> axes = mStrategyPanel.getLimitableAxes();
		mReviewTab.setSegmentSelectionListener(this, axes );
		mReviewTab.setSegment(new Limit()); // clear first
		mReviewTab.setSegmentSelection(true);
	}

	@Override
	public void onSegmentSelected(final Limit aSegment) {
		this.mSegment = aSegment;
		this.mReviewTab.setSegment(aSegment);
	}
	/**
	 * The user has selected a new strategy, show it's UI, so the user can select parameters.
	 */
	private void onStrategyChanged(final StrategySelection aStrategySelection) {
		if (mStrategyPanel != null) {
			mainPanel.remove(mStrategyPanel);
			mStrategyPanel = null;
		}
		mStrategyPanel = (StrategySelection) aStrategySelection.getPanel();
		mainPanel.addTab("strategy settings", mStrategyPanel);
		invalidate();
	}

	public Tool getTool() {
		return mTool;
	}
	/**
	 * Create the strategy chain of command to execute on the model.
	 */
	public IStrategy getStrategy(final GCodeWriterStrategy anOutputStrategy, final IModel aModel) {
		int feedRateValue = ((SpinnerNumberModel)feedRate.getModel()).getNumber().intValue();
		anOutputStrategy.setFeedRate(feedRateValue);

		IStrategy cutStrategy = anOutputStrategy;
		ZLimitingStrategy zLimitingStep = null;
		if (mRoughing.isSelected()) {
			zLimitingStep = new ZLimitingStrategy(cutStrategy);
			cutStrategy = zLimitingStep;
		}

		double freeMovementHeightValue = ((SpinnerNumberModel)freeMovementHeight.getModel()).getNumber().doubleValue();

		if (mReal5Axis.isSelected()) {
			// 4. do real 5 axis cutting into real 4 axis cutting
			cutStrategy = new FollowSurfaceNormalCutStrategy(aModel, Axis.B, cutStrategy, getTool(), freeMovementHeightValue);
		}
		if (mReal4Axis.isSelected()) {
			// 3. do real 4 axis cutting into real 4 axis cutting
			cutStrategy = new FollowSurfaceNormalCutStrategy(aModel, Axis.A, cutStrategy, getTool(), freeMovementHeightValue);
		} else {
			// 3. do fake 4 axis cutting by cutting only straight down
			cutStrategy = new StraightZCutStrategy(aModel, cutStrategy, getTool(), freeMovementHeightValue);
		}

		// 1. and 2. move along first and second axis
		IStrategy strategy = mStrategyPanel.getStrategy(aModel, cutStrategy );

		if (mRoughing.isSelected()) {
			double aLayerHeightValue = ((SpinnerNumberModel)mLayerHeight.getModel()).getNumber().doubleValue();

			strategy = new LayerStrategy(aLayerHeightValue, zLimitingStep, strategy, mModel.getMaxZ());
		}
		return strategy;
	}

	@Override
	public String toString() {
		return mLabel;
	}
}
