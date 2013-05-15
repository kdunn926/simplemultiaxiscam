package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import biz.wolschon.cam.multiaxis.model.Collision;
import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * Simple strategy to receive a coordinate in X,Y,Z or Y,A or X,A and
 * to move Z to the topmost intersection
 * @author marcuswolschon
 *
 */
public class StraightZCutStrategy implements IStrategy {

	private Axis mFreeAxis;
	private IModel mModel;
	private IStrategy mNext;

	/**
	 * 
	 * @param model
	 * @param freeaxis the value for this axis has not been determined by a previous strategy
	 */
	public StraightZCutStrategy(IModel model, Axis freeaxis, IStrategy next) {
		this.mModel = model;
		this.mFreeAxis = freeaxis;
		this.mNext = next;
		if (!freeaxis.isLinearAxis()) {
			//TODO: support a free A/B axis
			throw new IllegalArgumentException("Only linear free axis are supported");
		}
		if (freeaxis == Axis.Y) {
			//TODO: support a free Y axis
			throw new IllegalArgumentException("a free Y axis is not yet supported");
		}
	}

	@Override
	public void runStrategy(final double startLocation[]) throws IOException {
		//TODO: support more then just locations in Y+A
		startLocation[mFreeAxis.ordinal()] = 0; // center of rotation
		Vector3D direction = new Vector3D(0d,0d,-1d); //TODO: calculate from the value of the A axis
		List<Collision> collisions = getModel().getCollisions(new Vector3D(startLocation[0], startLocation[1], startLocation[2]), direction);
		if (collisions.size() == 0) {
			System.out.println("hole detected at " + startLocation[0] + ", " + startLocation[1] + ", " + startLocation[2] + ", " + startLocation[3]);
			return; // TODO: cut all the way through (hole)
		}
		if (collisions.size() > 2) {
			// we detected a non-convex patch
			//TODO: move Z to travel-height
			//TODO use a linear(X)+linear(Y)+StraightZ combination  along the plane of the start+end of this noncave patch to handle it
		}
		//TODO: sort collisions by distance

		startLocation[Axis.Z.ordinal()] = collisions.get(collisions.size() - 1).getCollisionPoint().getZ();
		this.mNext.runStrategy(startLocation);
	}

	protected IModel getModel() {
		return this.mModel;
	}

}
