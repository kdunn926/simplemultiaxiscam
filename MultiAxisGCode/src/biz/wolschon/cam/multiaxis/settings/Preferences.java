/**
 * 
 */
package biz.wolschon.cam.multiaxis.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author marcuswolschon
 *
 */
public class Preferences {

	private static final String TOOLS_DIR = "tools.dir";
	private Properties myPreferences = new Properties();
	
	public Preferences() {
		File preferencesFile = getPreferencesFile();
		setDefaultValues();
		if (preferencesFile.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(preferencesFile);
				try {
					myPreferences.load(fileInputStream);
				} finally {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				savePreferences();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void savePreferences() throws IOException {
		File preferencesFile = getPreferencesFile();
		if (!preferencesFile.getParentFile().mkdirs()) {
			System.err.println("cannot create " + preferencesFile.getParentFile().getAbsolutePath());
		}
		FileOutputStream out = new FileOutputStream(preferencesFile);
		try {
			myPreferences.store(out, "");
		} finally {
			out.close();
		}
	}

	/**
	 * Initialize {@link #myPreferences} with default values
	 */
	protected void setDefaultValues() {
		myPreferences.setProperty(TOOLS_DIR, (new File (new File(System.getProperty("user.home"), ".MultiAxisGCode"), "tools")).getAbsolutePath());
	}

	/**
	 * @return the directory where we store all tool definitions.
	 */
	public File getToolsDirectory() {
		return new File(myPreferences.getProperty(TOOLS_DIR));
	}
	/**
	 * @return
	 */
	protected File getPreferencesFile() {
		return new File (new File(System.getProperty("user.home"), ".MultiAxisGCode"), "MultiAxisGCode.properties");
	}
}
