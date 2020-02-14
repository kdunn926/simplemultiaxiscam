/**
 * 
 */
package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import biz.wolschon.cam.multiaxis.settings.Preferences;

/**
 * @author marcuswolschon
 *
 */
public class SettingsDialog extends JDialog {

	private Preferences prefs = new Preferences();
	private JPanel mOptionsPanel;
	private JTextField mToolsDir;

	public SettingsDialog(final MainFrame aMainFrame) {
		super(aMainFrame);
		setLayout(new BorderLayout());
		add(getOptionsPanel(), BorderLayout.CENTER);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent aArg0) {
				onOKClicked();				
			}
		});
		add(okButton, BorderLayout.SOUTH);
	}

	private Component getOptionsPanel() {
		if (mOptionsPanel == null) {
			mOptionsPanel = new JPanel(new GridLayout(1, 2));
			mToolsDir = new JTextField(prefs.getToolsDirectory().getAbsolutePath());
			mOptionsPanel.add(mToolsDir);
			mOptionsPanel.add(new JLabel("directory to store tools in"));
		}
		return mOptionsPanel;
	}

	protected void onOKClicked() {
		try {
			prefs.setToolsDirectory(new File(mToolsDir.getText()));
			prefs.savePreferences();
		} catch (IOException e) {
			javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR);
		}
		setVisible(false);
		
	}

}
