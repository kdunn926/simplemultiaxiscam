package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import biz.wolschon.cam.multiaxis.model.Collision;
import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.model.Triangle;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;
import biz.wolschon.cam.multiaxis.trigonometry.Trigonometry;

/**
 * This strategy receives a point in X,Z,A or Y,Z,A to cut.
 * It looks up the surface normal of the top-most triangle and moves the X,Y,Z,A axis to have the cutter parallel to that surface normal.
 * @author marcuswolschon
 *
 */
public class FollowSurfaceNormalCutStrategy extends StraightZCutStrategy {

	/**
	 * Given an already determined position for a rotational axis, try to move all axis to have hit the
	 * surface along the surface normal of the final object being cut in the rotational plane of that axis.
	 */
	private Axis mRotationAxis;
	
	/**
	 * Given an already determined position for a rotational axis, try to move all axis to have hit the
	 * surface along the surface normal of the final object being cut in the rotational plane of that axis.
	 * @param aTool The tool we are cutting with. (to determine collisions of tool and part)
	 * @param aModel The part we are trying to mill. (to detemine the Z depth to cut)
	 * @param aRotationAxis the rotation axis(value determined by a previous strategy) we are using
	 * @param aNext The next strategy to call
	 * @param aFreeMovementHeight z-position that never collides with any part of the object.
	 */
	public FollowSurfaceNormalCutStrategy(IModel aModel, final Axis aRotationAxis, final IStrategy aNext, final Tool aTool, final double aFreeMovementHeight) {
		super(aModel, aNext, aTool, aFreeMovementHeight);
		if (aRotationAxis.isLinearAxis()) {
			throw new IllegalArgumentException("no a rotation axis");
		}
		if (aTool == null) {
			throw new IllegalArgumentException("no tool given");
		}
		this.mRotationAxis= aRotationAxis;
	}

	/**
	 * The usual case of cutting a point on the (convex) surface.
	 * @param aCollision The point of cutting.
	 * @param aStartLocation the tool coordinates (X,Y,Z, possibly A, B and maybe even C rotational axis too) to reach #aCollision
	 */
	@Override
	protected void runStrategyCollision(final double aStartLocation[], final Collision aCollision, final Rotation aRotA, final boolean isCutting) throws IOException {
		Triangle polygon = aCollision.getCollidingPolygon();
		Vector3D normal	 = polygon.getNormal();
		//System.out.println("DEBUG: FollowSurfaceNormalCutStrategy normal=" + normal.toString());
		//Vector3D point   = aCollision.getCollisionPoint(); 

		Vector3D collisionPoint = aCollision.getCollisionPoint();
		double childLocation[] = Arrays.copyOf(aStartLocation, aStartLocation.length);
		childLocation[Axis.X.ordinal()] = collisionPoint.getX();
		childLocation[Axis.Y.ordinal()] = collisionPoint.getY();
		childLocation[Axis.Z.ordinal()] = collisionPoint.getZ();
		if (childLocation.length > Axis.A.ordinal() && mRotationAxis == Axis.A) {
			childLocation[Axis.A.ordinal()] = 0;
		}
		if (childLocation.length > Axis.B.ordinal() && mRotationAxis == Axis.B) {
			childLocation[Axis.B.ordinal()] = 0;
		}

		//do inverse kinematic using the surface normal at aCollision point as the direction we are trying to make contact from
		Trigonometry.inverseToolKinematic4Axis(childLocation, mRotationAxis, normal, getTool());

		//TODO: check for collision again and use original aStartLocation if thes differ (= with the new angle for mRotationAxis some part of our tool collides with the part) 
		Rotation rotA = Axis.A.getRotation(childLocation[Axis.A.ordinal()]);
		SortedSet<Collision> newCollisions = diveForCollisions(aStartLocation, rotA);
		if (newCollisions.size() == 0) {
			System.err.println("After inverse kinematics, we no longer collide with the part.");
			super.runStrategyCollision(aStartLocation, aCollision, aRotA, isCutting);
			return;
		}
		Collision topmost = newCollisions.first();
		if (!isSameCollision(topmost, aCollision)) {
			System.err.println("After inverse kinematics, we no longer collide with the part in the same spot. regular=" + aCollision + " now=" + topmost);
			super.runStrategyCollision(aStartLocation, aCollision, aRotA, isCutting);
			return;
		}

		if (!isCutting) {
			// we still run inverseToolKinematic4Axis to end up with the same location on all other axis
			childLocation[Axis.Z.ordinal()] = getFreeMovementHeight();
		}
		getNextStrategy().runStrategy(childLocation, isCutting);
	}

	/**
	 * 
	 * @param aTopmost
	 * @param aCollision
	 * @return true if both collisions are the same within reasonable tollerances.
	 */
	private boolean isSameCollision(Collision aTopmost, Collision aCollision) {
		//TODO: happens when it should not happen if (aTopmost.getCollidingPolygon() != aCollision.getCollidingPolygon()) {
		//	System.err.println("colliding with another polygon");
		//	return false;
		//}
		double diff = Math.abs(aTopmost.getToolLocation() - aCollision.getToolLocation());
		
		if (diff > 0.001) {
			System.err.println("colliding using another part of the tool");
			return false;
		}

		// compare coordinates using an epsilon
//		final double epsilon = 0.2d;
//		double distance = Math.abs(aTopmost.getCollisionPoint().getX() - aCollision.getCollisionPoint().getX());
//		distance += Math.abs(aTopmost.getCollisionPoint().getY() - aCollision.getCollisionPoint().getY());
//		distance += Math.abs(aTopmost.getCollisionPoint().getZ() - aCollision.getCollisionPoint().getZ());
//		if(distance > epsilon) {
//			System.err.println("colliding in another spot.");
//			return false;
//		}
		return true;
	}

}
