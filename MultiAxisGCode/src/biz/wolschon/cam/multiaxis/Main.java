/**
 * 
 */
package biz.wolschon.cam.multiaxis;

import java.io.FileWriter;
import java.io.IOException;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.strategy.FollowSurfaceNormalCutStrategy;
import biz.wolschon.cam.multiaxis.strategy.GCodeWriterStrategy;
import biz.wolschon.cam.multiaxis.strategy.IStrategy;
import biz.wolschon.cam.multiaxis.strategy.LinearStrategy;
import biz.wolschon.cam.multiaxis.strategy.StraightZCutStrategy;
import biz.wolschon.cam.multiaxis.ui.Wizard1Loader;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * @author marcuswolschon
 *
 */
public class Main implements Wizard1Loader.Listener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Main();
		
	}
	
	public  Main() {


		try {
			Wizard1Loader step1 = new Wizard1Loader(this);
			step1.pack();
			step1.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFileLoaded(IModel model) throws IOException {
		// TODO show second screen

		FileWriter outfile = new FileWriter("/tmp/out.gcode");
		try {
			GCodeWriterStrategy out = new GCodeWriterStrategy(outfile);

			IStrategy cutStrategy = new FollowSurfaceNormalCutStrategy(model, Axis.X, out);
			IStrategy collisionStrategy = new StraightZCutStrategy(model, Axis.X, cutStrategy);

			IStrategy aroundYAxis = new LinearStrategy(model, Axis.A, 0.2d, collisionStrategy);

			LinearStrategy alongYAxis = new LinearStrategy(model, Axis.Y, 0.1d, aroundYAxis);
			double[] startLocation = new double[] {
					model.getCenterX(),
					model.getMinY(),
					model.getCenterZ(),
					0 // A axis
			};
			alongYAxis.runStrategy(startLocation);
		} finally {
			outfile.close();
		}
	}

}
