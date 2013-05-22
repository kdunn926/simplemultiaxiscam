package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.ui.Wizard1Loader;

public class MainFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Wizard1Loader step1 = new Wizard1Loader(new Wizard1Loader.Listener() {
				
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

	private ModelReviewPanel mReviewTab;
	private StrategyCreationPanel mStrategyTab;
	private GCodePanel mGCodeTab;
	/**
	 * Create the frame.
	 */
	public MainFrame(final IModel aModel) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		mReviewTab = new ModelReviewPanel(aModel);
		tabbedPane.addTab("review model", mReviewTab);

		mStrategyTab = new StrategyCreationPanel();
		tabbedPane.addTab("cutting strategy", mStrategyTab);

		mGCodeTab = new GCodePanel(aModel, mReviewTab, mStrategyTab);
		tabbedPane.addTab("generate G-Code", mGCodeTab);
	}

}
