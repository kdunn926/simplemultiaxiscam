package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
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

	private IModel mModel;
	private IStrategy mNext;

	/**
	 * 
	 * @param model
	 */
	public StraightZCutStrategy(IModel model, IStrategy next) {
		this.mModel = model;
		this.mNext = next;
	}

	@Override
	public void runStrategy(final double aStartLocation[]) throws IOException {
		//TODO: support more then just locations in Y+A

		// getCollisions uses a 3D location and direction whereas startLocation
		// is a 4 dimensional machine coordinate including 2 rotational axis

		// direction our cutter is coming from (calculated using A axis)
		Vector3D direction = new Vector3D(0d,0d,-1d);
		Rotation rotA = Axis.A.getRotation(aStartLocation[Axis.A.ordinal()]);

		direction = rotA.applyInverseTo(direction);
		List<Collision> collisions = getModel().getCollisions(new Vector3D(aStartLocation[0], aStartLocation[1], aStartLocation[2]), direction);
		if (collisions.size() == 0) {
			runStrategyHole(aStartLocation);
			return; // TODO: cut all the way through (hole)
		}
		if (collisions.size() > 2) {
			// we detected a non-convex patch
			runStrategyNonConvex(aStartLocation, collisions);
		}
		//TODO: sort collisions by distance

		runStrategyCollision(aStartLocation, collisions.get(collisions.size() - 1));
	}
     protected void runStrategyHole(final double aStartLocation[]) {
		System.out.println("hole detected at X" + aStartLocation[0] + " Y" + aStartLocation[1] + " Z" + aStartLocation[2] + " A" + aStartLocation[3]);	
	}
	protected void runStrategyNonConvex(final double aStartLocation[], final List<Collision> aCollisionList) {
			//TODO: move Z to travel-height
			//TODO use a linear(X)+linear(Y)+StraightZ combination  along the plane of the start+end of this noncave patch to handle it
	}
	protected void runStrategyCollision(final double aStartLocation[], final Collision aCollision) throws IOException {
		aStartLocation[Axis.Z.ordinal()] = aCollision.getCollisionPoint().getZ();
		this.mNext.runStrategy(aStartLocation);
	}

	protected IModel getModel() {
		return this.mModel;
	}


	@Override
	public void endStrategy()  throws IOException {
		this.mNext.endStrategy();
	}
}
