package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IProgressListener;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.views.GCodeModel.GCodeLine;

/**
 * Panel to start G-Code generation and show the generated G-Code.
 */
public class GCodePanel extends JPanel implements IProgressListener {

	/**
	 * For serializable.
	 */
	private static final long serialVersionUID = -6893843077156524310L;
	

	/**
	 * The 3D model we are working with.	
	 */
	private IModel mModel;
	private JList codeList;
	private JButton mSaveButton;
	private GCodeModel codeListModel;
	private StrategyStepsPanel mStrategyPanel;
	private ModelReviewPanel mReviewTab;
	private JProgressBar mProgressBar;
	private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();

	/**
	 * Create the panel.<br/>
	 * If a ModelReviewPanel is given, a click inside the g-code list will show that tool position in the ModelReviewPanel.
	 * @param reviewTab may be null
	 */
	public GCodePanel(final IModel aModel, final ModelReviewPanel aReviewTab, final StrategyStepsPanel aStrategySteps) {
		this.mModel = aModel;
		this.mReviewTab = aReviewTab;
		this.mStrategyPanel = aStrategySteps;
		setLayout(new BorderLayout(0, 0));
		
		mSaveButton = new JButton("save");
		mSaveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onSaveFile();
			}
		});
		add(mSaveButton, BorderLayout.SOUTH);
		mSaveButton.setEnabled(false);
		
		mProgressBar = new JProgressBar();
		add(mProgressBar, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		final JButton btnGenerateGcode = new JButton("Generate G-Code");
		scrollPane.setColumnHeaderView(btnGenerateGcode);
		btnGenerateGcode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnGenerateGcode.setEnabled(false);
				Thread t = new Thread() {
					public void run() {
						mSaveButton.setEnabled(true);
						onGenerateGCode();
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								btnGenerateGcode.setEnabled(true);
							}
						});
					}
				};
				t.start();
				
				
			}
		});
		
		codeList = new JList();
		codeListModel = new GCodeModel();
		codeList.setModel(codeListModel);
		codeList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				GCodeLine line = (GCodeLine) codeList.getSelectedValue();
				if (line == null) {
					return;
				}
				double[] toolLocation = line.getToolLocation();
				if (toolLocation == null) {
					return; // header
				}
				System.out.println("selected: '" + line + "' " + Arrays.toString(toolLocation));
				mReviewTab.setTool(line.getTool());
				mReviewTab.setToolLocation(toolLocation);
				
			}
		});
		scrollPane.setViewportView(codeList);
	}

	/**
	 * @return the codeListModel
	 */
	public GCodeModel getCodeListModel() {
		return codeListModel;
	}

	protected void onSaveFile() {
		mSaveButton.setEnabled(false);
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "G-Code";
			}
			
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".ngc");
			}
		});
		File testdir = new File(System.getProperty("user.dir"), "test");
		chooser.setCurrentDirectory(testdir);
		chooser.setSelectedFile(new File(testdir, "cube.ngc"));
		chooser.showSaveDialog(this);
		final File f = chooser.getSelectedFile();
		Thread t = new Thread() {
			public void run() {
				//do the actual saving
				try {
					FileWriter outfile = new FileWriter(f);
					try {
						int lines = codeListModel.getSize();
						String newline = System.getProperty("line.separator");
						for (int i = 0; i < lines; i++) {
							outfile.write(codeListModel.get(i).toString());
							outfile.write(newline);
						}
					} finally {
						outfile.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				EventQueue.invokeLater(new Runnable() {
							public void run() {
								mSaveButton.setEnabled(true);
							}
						});

			}
		};
		t.start();
		
	}
	protected void onGenerateGCode() {
		try {
			// always write to a temporary file
			// TODO: use this file for codeListModel to lift the 32767 array element limit and cut down on memory usage
			FileWriter outfile = new FileWriter("/tmp/out-" + System.currentTimeMillis() + ".gcode");
			List<StrategyCreationPanel> steps = mStrategyPanel.getAllStrategies();
			try {
				for (StrategyCreationPanel strategyStep : steps) {

					final Tool tool = strategyStep.getTool();
					codeListModel.clear();
					GCodeWriterStrategy out = new GCodeWriterStrategy(outfile) {
						@Override
						protected void writeCodeLine(final String aLine, final double[] aLocation) throws IOException {
							super.writeCodeLine(aLine, aLocation);
							codeListModel.addElement(new GCodeLine(aLine, aLocation, tool));
						}
					};
					IStrategy strategy = strategyStep.getStrategy(out, mModel);
					double[] startLocation = new double[] {
							mModel.getCenterX(),
							mModel.getCenterY(),
							mModel.getCenterZ(),
							0 // A axis
							// no B axis
					};
					strategy.addProgressListener(GCodePanel.this);
					strategy.runStrategy(startLocation, true);
					strategy.endStrategy();

				}
			} finally {
				outfile.flush();
				outfile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onProgressChanged(IStrategy aSender, long aProgress,
			long aMaximum) {
		if (aMaximum > Integer.MAX_VALUE) {

			mProgressBar.setMaximum(Integer.MAX_VALUE);
			mProgressBar.setValue((int) (aProgress * Integer.MAX_VALUE / aMaximum));
		} else {
			mProgressBar.setMaximum((int) aMaximum);
			mProgressBar.setValue((int) aProgress);
		}
		mProgressBar.setStringPainted(true);
		mProgressBar.setString(PERCENT_FORMAT.format(((double)aProgress)/aMaximum));
		
	}

}
