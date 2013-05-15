package biz.wolschon.cam.multiaxis.views;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JList;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.FollowSurfaceNormalCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.strategy.LinearStrategy;
import biz.wolschon.cam.multiaxis.strategy.StraightZCutStrategy;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

public class GCodePanel extends JPanel {

	private IModel mModel;
	private JList codeList;
	private DefaultListModel codeListModel;

	/**
	 * Create the panel.
	 */
	public GCodePanel(final IModel model) {
		this.mModel = model;
		setLayout(new BorderLayout(0, 0));
		
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
		codeListModel = new DefaultListModel();
		codeList.setModel(codeListModel);
		scrollPane.setViewportView(codeList);
	}

	protected void onGenerateGCode() {
		try {
			FileWriter outfile = new FileWriter("/tmp/out.gcode");
			try {
				GCodeWriterStrategy out = new GCodeWriterStrategy(outfile) {
					protected void writeCodeLine(final String s) throws IOException {
						super.writeCodeLine(s);
						codeListModel.addElement(s);
					}
				};

				IStrategy cutStrategy = new FollowSurfaceNormalCutStrategy(mModel, Axis.X, out);
				IStrategy collisionStrategy = new StraightZCutStrategy(mModel, Axis.X, cutStrategy);

				IStrategy aroundAAxis = new LinearStrategy(mModel, Axis.A, 10d, collisionStrategy);

				LinearStrategy alongYAxis = new LinearStrategy(mModel, Axis.Y, 1.1d, aroundAAxis);
				double[] startLocation = new double[] {
						mModel.getCenterX(),
						mModel.getMinY(),
						mModel.getCenterZ(),
						0 // A axis
				};
				alongYAxis.runStrategy(startLocation);
			} finally {
				outfile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
