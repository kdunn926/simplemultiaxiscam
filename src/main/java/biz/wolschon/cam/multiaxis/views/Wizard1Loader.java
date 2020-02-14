/**
 * 
 */
package biz.wolschon.cam.multiaxis.views;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.model.STLModel;

/**
 * @author marcuswolschon
 *
 */
public class Wizard1Loader  {

	public interface Listener {

		void onFileLoaded(IModel model) throws IOException;

	}

	private Listener mListener;

	public Wizard1Loader(Listener listener) throws IOException {
		this.mListener = listener;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "STL";
			}
			
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".stl");
			}
		});
		File testdir = new File(System.getProperty("user.dir"), "test");
		chooser.setCurrentDirectory(testdir);
		chooser.setSelectedFile(new File(testdir, "cube.stl"));
		chooser.showOpenDialog(null);
		File f = chooser.getSelectedFile();
		STLModel model = new STLModel(f);
		mListener.onFileLoaded(model);
	}

}
