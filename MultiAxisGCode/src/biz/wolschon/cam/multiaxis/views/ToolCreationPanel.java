package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import biz.wolschon.cam.multiaxis.tools.Tool;

public class ToolCreationPanel extends JPanel {
	/**
	 * For {@link Serializable}.
	 */
	private static final long serialVersionUID = -8813852856521388245L;
	/**
	 * List to select a tool from.<br/>
	 * Uppon selecting the tool #onToolSelected() takes care of displaying the UI for it's parameters
	 */
	private JList mToolTypes;
	/**
	 * Currently selected tool and also the JPanel for entering it's parameters.
	 */
	private ToolSelection mToolPanel;


	/**
	 * Abstract interface for a selectable tool and it's UI.
	 */
	private static abstract class ToolSelection extends JPanel {
		/**
		 * For {@link Serializable}.
		 */
		private static final long serialVersionUID = -4892718902569153906L;
		private String mLabel;
		public ToolSelection(final String aLabel) {
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
		 * Create the tool according to the user entered parameters.
		 */
		public abstract Tool getTool();
	}
	/**
	 * Our default tool (a ball shaped cutter).
	 */
	private final ToolSelection TOOL0 = new ToolSelection("Ball Nose") {
		/**
		 * For {@link Serializable}.
		 */
		private static final long serialVersionUID = 2957137275776885590L;
		private JTextField name = new JTextField("new tool");
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
			panel.setLayout(new GridLayout(4, 1));

			panel.add(new JLabel("name:"), null);
			panel.add(name, null);

			panel.add(new JLabel("ball diameter:"), null);
			panel.add(diameter, null);

			panel.add(new JLabel("full length:"), null);
			panel.add(length, null);

			panel.add(new JLabel("shaft diameter:"), null);
			panel.add(shaftDiameter, null);

			return panel;
		}
		public Tool getTool() {
			return Tool.createBallCutter(name.getText(), Double.parseDouble(diameter.getText()),
					Double.parseDouble(length.getText()),
					Double.parseDouble(shaftDiameter.getText()));
		}
	};

	/**
	 * A flat cutter tool.
	 */
	private final ToolSelection TOOL1 = new ToolSelection("Flat Cutter") {
		/**
		 * For {@link Serializable}.
		 */
		private static final long serialVersionUID = -7515866218730158209L;
		private JTextField name = new JTextField("new tool");
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

			panel.add(new JLabel("name:"), null);
			panel.add(name, null);

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
			return Tool.createFlatCutter(name.getText(), Double.parseDouble(diameter.getText()),
					Double.parseDouble(cutterLength.getText()),
					Double.parseDouble(length.getText()),
					Double.parseDouble(shaftDiameter.getText()));
		}
	};
	private JButton mSaveButton;

	public ToolCreationPanel() {
		mToolTypes = new JList(new ToolSelection[] {TOOL0, TOOL1});
		mToolTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mToolTypes.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				ToolSelection tool = (ToolSelection) mToolTypes.getSelectedValue();
				onToolChanged(tool);
				
			}
		});
		add(new JScrollPane(mToolTypes), BorderLayout.NORTH);

		mToolTypes.setSelectedValue(TOOL0, true);
		//mToolTypes.setVisibleRowCount(1);
		onToolChanged(TOOL0);
		add(getSaveButton(), BorderLayout.SOUTH);
	}

	/**
	 * @return
	 */
	public JButton getSaveButton() {
		if (mSaveButton == null) {
			mSaveButton = new JButton("Save");
		}
		return mSaveButton;
	}

	public Tool getTool() {
		return mToolPanel.getTool();
	}

	/**
	 * The user has selected a new tool, show it's UI, so the user can select parameters.
	 */
	private void onToolChanged(final ToolSelection aToolSelection) {
		if (mToolPanel != null) {
			remove(mToolPanel);
			mToolPanel = null;
		}
		mToolPanel = (ToolSelection) aToolSelection.getPanel();
		add(mToolPanel, BorderLayout.CENTER);
		invalidate();
	}
}
