/**
 * 
 */
package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author marcuswolschon
 *
 */
public class StrategyStepsPanel extends JPanel {

	public interface CurrentStrategyStepListener {

		void onStrategyStepChanged(StrategyCreationPanel aNewStep);
	}

	private StrategyCreationPanel mCurrentStep;
	private CurrentStrategyStepListener mListener;
	private JList mStrategySteps;
	private DefaultListModel mStrategyStepsModel;
	private JButton mAddButton;

	public StrategyCreationPanel getCurrentStep() {
		return mCurrentStep;
	}

	public void setCurrentStep(final StrategyCreationPanel aCurrentStep) {
		mCurrentStep = aCurrentStep;
		if (getListener() != null) {
			getListener().onStrategyStepChanged(getCurrentStep());
		}
	}

	public StrategyStepsPanel() {
		setLayout(new BorderLayout());
		mStrategySteps = new JList();
		mStrategyStepsModel = new DefaultListModel();
		mStrategySteps.setModel(mStrategyStepsModel);
		mStrategySteps.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				StrategyCreationPanel s = (StrategyCreationPanel) mStrategySteps.getSelectedValue();
				setCurrentStep(s);

			}
		});
		add(new JScrollPane(mStrategySteps), BorderLayout.CENTER);
		add(getAddButton(), BorderLayout.SOUTH);
	}

	private JButton getAddButton() {
		if (mAddButton == null) {
			mAddButton = new JButton("add step");
			mAddButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent aIgnored) {
					addStrategyStep(new StrategyCreationPanel("new"));
				}
			});
		}
		return mAddButton;
	}

	/**
	 * @return the listener
	 */
	public CurrentStrategyStepListener getListener() {
		return mListener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(CurrentStrategyStepListener listener) {
		mListener = listener;
	}

	public void addStrategyStep(final StrategyCreationPanel aCurrentStrategyTab) {
		this.mStrategyStepsModel.addElement(aCurrentStrategyTab);
		if (this.mStrategyStepsModel.size() == 1) {
			this.mStrategySteps.setSelectedValue(aCurrentStrategyTab, true);
		}
	}

	public List<StrategyCreationPanel> getAllStrategies() {
		int size = mStrategyStepsModel.size();
		List<StrategyCreationPanel> retval = new ArrayList<StrategyCreationPanel>(size);
		for (int i = 0; i < size; i++) {
			retval.add((StrategyCreationPanel) mStrategyStepsModel.get(i));
		}
		return retval;
	}
}
