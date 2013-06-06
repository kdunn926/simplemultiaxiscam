package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.settings.Preferences;
import biz.wolschon.cam.multiaxis.tools.ToolRepository;
import biz.wolschon.cam.multiaxis.views.StrategyStepsPanel.CurrentStrategyStepListener;

import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame implements CurrentStrategyStepListener {

	/**
	 * For {@link Serializable}.
	 */
	private static final long serialVersionUID = -582634794848211670L;
	private JSplitPane contentPane;
	private JPanel mLeftPane;
	private ToolRepository mTools = new ToolRepository(new Preferences());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			/*Wizard1Loader step1 = */new Wizard1Loader(new Wizard1Loader.Listener() {
				
				@Override
				public void onFileLoaded(final IModel aModel) throws IOException {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								MainFrame frame = new MainFrame(aModel);
								frame.pack();
								frame.setVisible(true);
								frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					
				}
			});
			//step1.pack();
			//step1.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    /**
	 * Showing the model and tool position for
	 * the currently selected line of G-Code
	 */
	private ModelReviewPanel mReviewTab;
	/**
	 * Currently selected strategy step
	 * e.g. roughing, finishing or contour.
	 */
	private StrategyCreationPanel mCurrentStrategyTab;
	/**
	 * Showing the generated G-Code,
	 * buttons to start code generation,
	 * progress bar and save button for code.
	 */
	private GCodePanel mGCodeTab;
	private StrategyStepsPanel mStrategySteps;
	/**
	 * Create the frame.
	 */
	public MainFrame(final IModel aModel) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setContentPane(contentPane);

		JSplitPane contentPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(contentPane2, JSplitPane.RIGHT);
		
		mLeftPane = new JPanel();
		mLeftPane.setLayout(new BorderLayout());
		mLeftPane.add(new JScrollPane(getStrategyStepsList(aModel)),  BorderLayout.NORTH);
		contentPane.add(mLeftPane, JSplitPane.LEFT);

		contentPane2.add(getModelReviewTab(aModel), JSplitPane.TOP);

		StrategyCreationPanel roughingStep = new StrategyCreationPanel("roughing", mTools, aModel, getModelReviewTab(aModel));
		StrategyCreationPanel finishingStep = new StrategyCreationPanel("finishing", mTools, aModel, getModelReviewTab(aModel));
		mStrategySteps.addStrategyStep(roughingStep);
		mStrategySteps.addStrategyStep(finishingStep);
		//onStrategyStepChanged(finishingStep);
		roughingStep.setRoughing(true);

		mGCodeTab = new GCodePanel(aModel, getModelReviewTab(aModel), mStrategySteps);
		contentPane2.add(mGCodeTab, JSplitPane.BOTTOM);
		mReviewTab.setGCodeModel(mGCodeTab.getCodeListModel());
		
		
		contentPane2.setDividerLocation(0.2);
		contentPane.setDividerLocation(0.3);
	}

	/**
	 * @param aModel
	 * @return 
	 */
	protected ModelReviewPanel getModelReviewTab(final IModel aModel) {
		if (mReviewTab == null) {
			mReviewTab = new ModelReviewPanel(aModel);
			mReviewTab.setPreferredSize(new Dimension(800, 600));
		}
		return mReviewTab;
	}

	private JComponent getStrategyStepsList(final IModel aModel) {
		if (mStrategySteps == null) {
			mStrategySteps = new StrategyStepsPanel(mTools, aModel, getModelReviewTab(aModel));
			mStrategySteps.setListener(this);
		}
    	return mStrategySteps;
	}

	public void onStrategyStepChanged(final StrategyCreationPanel aStep) {
		if (mCurrentStrategyTab != null) {
			mLeftPane.remove(mCurrentStrategyTab);
		}
		mCurrentStrategyTab = aStep;
		if (mCurrentStrategyTab != null) {
			mLeftPane.add(mCurrentStrategyTab, BorderLayout.CENTER);
			mCurrentStrategyTab.setBorder(new TitledBorder(null, aStep.toString(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
			invalidate();
			repaint();
		}
	}
}
