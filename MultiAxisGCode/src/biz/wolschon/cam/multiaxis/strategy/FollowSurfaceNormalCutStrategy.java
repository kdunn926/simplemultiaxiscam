package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * This strategy receives a point in X,Z,A or Y,Z,A to cut.
 * It looks up the surface normal of the top-most triangle and moves the X,Y,Z,A axis to have the cutter parallel to that surface normal.
 * @author marcuswolschon
 *
 */
public class FollowSurfaceNormalCutStrategy implements IStrategy {

	private Axis mFreeAxis;
	private IModel mModel;
	private IStrategy mNext;

	/**
	 * 
	 * @param model
	 * @param freeaxis the value for this axis has not been determined by a previous strategy
	 */
	public FollowSurfaceNormalCutStrategy(IModel model, final Axis freeaxis, final IStrategy next) {
		this.mFreeAxis = freeaxis;
		this.mNext = next;
		this.mModel = model;
		if (freeaxis == Axis.A) {
			//TODO: support a free A axis
			throw new IllegalArgumentException("a free A axis is not yet supported");
		}
		if (freeaxis == Axis.Y) {
			//TODO: support a free Y axis
			throw new IllegalArgumentException("a free Y axis is not yet supported");
		}
	}

	@Override
	public void runStrategy(double[] startLocation) throws IOException {
		// TODO do inverse kinematic
		this.mNext.runStrategy(startLocation);
	}


}
