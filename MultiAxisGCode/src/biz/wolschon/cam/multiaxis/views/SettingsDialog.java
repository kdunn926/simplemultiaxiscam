/**
 * 
 */
package biz.wolschon.cam.multiaxis.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * @author marcuswolschon
 *
 */
public class SettingsDialog extends JDialog {

	public SettingsDialog(final MainFrame aMainFrame) {
		super(aMainFrame);
		setLayout(new BorderLayout());
		add(new JPanel(), BorderLayout.CENTER);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent aArg0) {
				onOKClicked();				
			}
		});
		add(okButton, BorderLayout.SOUTH);
	}

	protected void onOKClicked() {
		setVisible(false);
		
	}

}
