package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;

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
	private IModel mModel;
	private IStrategy mNext;
	private Tool mTool;

	/**
	 * Given an already determined position for a rotational axis, try to move all axis to have hit the
	 * surface along the surface normal of the final object being cut in the rotational plane of that axis.
	 * @param aModel
	 * @param aRotationAxis the rotation axis(value determined by a previous strategy) we are using
	 */
	public FollowSurfaceNormalCutStrategy(IModel aModel, final Axis aRotationAxis, final IStrategy aNext, final Tool aTool) {
		super(aModel, aNext);
		if (aRotationAxis.isLinearAxis()) {
			throw new IllegalArgumentException("no a rotation axis");
		}
		if (aTool == null) {
			throw new IllegalArgumentException("no tool given");
		}
		this.mRotationAxis= aRotationAxis;
		this.mNext = aNext;
		this.mModel = aModel;
		this.mTool  = aTool;
	}

	@Override
	protected void runStrategyCollision(final double aStartLocation[], final Collision aCollision) throws IOException {
		//TODO: do inverse kinematic using the surface normal at aCollision
		Triangle polygon = aCollision.getCollidingPolygon();
		Vector3D normal	 = polygon.getNormal();
		Vector3D point   = aCollision.getCollisionPoint(); 

		aStartLocation[Axis.Z.ordinal()] = aCollision.getCollisionPoint().getZ();
		Trigonometry.inverseToolKinematic4Axis(aStartLocation, mRotationAxis, normal, mTool);

		this.mNext.runStrategy(aStartLocation);
	}

}
