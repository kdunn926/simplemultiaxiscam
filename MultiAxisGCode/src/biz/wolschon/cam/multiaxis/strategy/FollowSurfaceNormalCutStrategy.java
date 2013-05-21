package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;

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
	 */
	public FollowSurfaceNormalCutStrategy(IModel aModel, final Axis aRotationAxis, final IStrategy aNext, final Tool aTool) {
		super(aModel, aNext, aTool);
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
	protected void runStrategyCollision(final double aStartLocation[], final Collision aCollision) throws IOException {
		Triangle polygon = aCollision.getCollidingPolygon();
		Vector3D normal	 = polygon.getNormal();
		//System.out.println("DEBUG: FollowSurfaceNormalCutStrategy normal=" + normal.toString());
		//Vector3D point   = aCollision.getCollisionPoint(); 

		aStartLocation[Axis.X.ordinal()] = aCollision.getCollisionPoint().getX();
		aStartLocation[Axis.Y.ordinal()] = aCollision.getCollisionPoint().getY();
		aStartLocation[Axis.Z.ordinal()] = aCollision.getCollisionPoint().getZ();
		aStartLocation[Axis.A.ordinal()] = 0;

		//do inverse kinematic using the surface normal at aCollision point as the direction we are trying to make contact from
		Trigonometry.inverseToolKinematic4Axis(aStartLocation, mRotationAxis, normal, getTool());
		//TODO: check for collision again and use original aStartLocation if thes differ (= with the new angle for mRotationAxis some part of our tool collides with the part) 

		getNextStrategy().runStrategy(aStartLocation);
	}

}
