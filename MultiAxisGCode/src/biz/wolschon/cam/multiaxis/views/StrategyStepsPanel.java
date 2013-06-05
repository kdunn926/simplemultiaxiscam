/**
 * 
 */
package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.tools.ToolRepository;

/**
 * @author marcuswolschon
 *
 */
public class StrategyStepsPanel extends JPanel {

	/**
	 * For {@link Serializable}.
	 */
	private static final long serialVersionUID = -6623122150317818219L;

	public interface CurrentStrategyStepListener {

		/**
		 * @param aNewStep MAY BE NULL if none is selcted
		 */
		void onStrategyStepChanged(StrategyCreationPanel aNewStep);
	}

	private IModel mModel;

	private final ToolRepository mToolsRepository;
	private StrategyCreationPanel mCurrentStep;
	private CurrentStrategyStepListener mListener;
	private JList mStrategySteps;
	private DefaultListModel mStrategyStepsModel;
	private JButton mAddButton;

	private ModelReviewPanel mReviewPanel;

	public StrategyCreationPanel getCurrentStep() {
		return mCurrentStep;
	}

	public void setCurrentStep(final StrategyCreationPanel aCurrentStep) {
		mCurrentStep = aCurrentStep;
		if (getListener() != null) {
			getListener().onStrategyStepChanged(getCurrentStep());
		}
	}

	public StrategyStepsPanel(final ToolRepository aToolsRepository, final IModel aModel, final ModelReviewPanel aReviewPanel) {
		this.mToolsRepository = aToolsRepository;
		this.mModel = aModel;
		this.mReviewPanel = aReviewPanel;
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
		mStrategySteps.addMouseListener(new MouseAdapter() {
			 public void mousePressed(MouseEvent e){
				 StrategyCreationPanel s = (StrategyCreationPanel) mStrategySteps.getSelectedValue();
				 if (e.isPopupTrigger() && s != null)
					 showPopupMenu(e, s);
			 }

			 public void mouseReleased(MouseEvent e){
				 StrategyCreationPanel s = (StrategyCreationPanel) mStrategySteps.getSelectedValue();
				 if (e.isPopupTrigger() && s != null)
					 showPopupMenu(e, s);
			 }
		});
		add(new JScrollPane(mStrategySteps), BorderLayout.CENTER);
		add(getAddButton(), BorderLayout.SOUTH);
	}

	protected void showPopupMenu(final MouseEvent anEvent, final StrategyCreationPanel aStep) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem removeItem = new JMenuItem("remove");
		removeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent aArg0) {
				if (JOptionPane.showConfirmDialog(null, "Delete step?") == JOptionPane.YES_OPTION) {
					removeStrategyStep(aStep);
				}
			}
		});
		menu.add(removeItem);
		JMenuItem renameItem = new JMenuItem("rename...");
		renameItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent aArg0) {
				String newLabel = JOptionPane.showInputDialog("label:", aStep.getLabel());
				if (newLabel != null) {
					aStep.setLabel(newLabel);
				}
			}
		});
		menu.add(renameItem);
		menu.show(anEvent.getComponent(), anEvent.getX(), anEvent.getY());
		
	}

	private JButton getAddButton() {
		if (mAddButton == null) {
			mAddButton = new JButton("add step");
			mAddButton.addActionListener(new ActionListener() {

								@Override
				public void actionPerformed(ActionEvent aIgnored) {
					addStrategyStep(new StrategyCreationPanel("new", mToolsRepository, mModel, mReviewPanel));
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
	public void removeStrategyStep(final StrategyCreationPanel aCurrentStrategyTab) {
		this.mStrategyStepsModel.removeElement(aCurrentStrategyTab);
		if (aCurrentStrategyTab == mCurrentStep) {
			if (this.mStrategyStepsModel.size() > 0) {
				this.mStrategySteps.setSelectedIndex(0);
			} else {
				setCurrentStep(null);
			}
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
