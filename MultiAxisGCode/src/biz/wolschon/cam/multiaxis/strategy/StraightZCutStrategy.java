package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import biz.wolschon.cam.multiaxis.model.Collision;
import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * Simple strategy to receive a coordinate in X,Y,Z or Y,A or X,A and
 * to move Z to the topmost intersection
 * @author marcuswolschon
 *
 */
public class StraightZCutStrategy implements IStrategy, IProgressListener {

	/**
	 * The part we are trying to mill.
	 */
	private IModel mModel;
	/**
	 * The next strategy to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 */
	private IStrategy mNext;
	/**
	 * The tool we are cutting with.
	 */
	private Tool mTool;
	/**
	 * Leave this much model around the cutter in each direction.
	 * (Used for roughing.)
	 */
	private double mSkinThickness;


	private IProgressListener mProgressListener;
	/**
	 * Z-position that never collides with any part of the object.
	 */
	private double mFreeMovementHeight;

	/**
	 * @param aTool The tool we are cutting with. (to determine collisions of tool and part)
	 * @param aModel The part we are trying to mill. (to detemine the Z depth to cut)
	 * @param aNext The next strategy to call
	 * @param aFreeMovementHeight z-position that never collides with any part of the object.
	 * 
	 */
	public StraightZCutStrategy(IModel aModel, IStrategy aNext, final Tool aTool, final double aFreeMovementHeight) {
		this.mModel = aModel;
		this.mNext = aNext;
		this.mTool  = aTool;
		this.mSkinThickness = 0;
		this.mFreeMovementHeight = aFreeMovementHeight;
	}

	/**
	 * @return The tool we are cutting with.
	 */
	public Tool getTool() {
		return mTool;
	}

	/**
	 * @return The part we are trying to mill.
	 */
	public IModel getModel() {
		return mModel;
	}

	/**
	 * The next strategy to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 * @return the next strategy to call.
	 */
	public IStrategy getNextStrategy() {
		return mNext;
	}

	/**
	 * Determines what is below the cutter
	 * (Honoring values for A and B axis in the location if such rotations have been set by a previous strategy)
	 * and calls #runStrategyHole (nothing below the cutter)
	 * #runStrategyNonConvex (more then 2 intersections with the object)
	 * or #runStrategyCollision
	 */
	@Override
	public void runStrategy(final double aStartLocation[], final boolean isCutting) throws IOException {
		Rotation rotA = Axis.A.getRotation(aStartLocation[Axis.A.ordinal()]);
		// collisions come sorted by distance
		SortedSet<Collision> collisions = diveForCollisions(aStartLocation, rotA);
		if (collisions.size() == 0) {
			// cut all the way through (hole)
			runStrategyHole(aStartLocation ,isCutting);
			return;
		}
		if (collisions.size() > 2) {
			// we detected a non-convex patch
			runStrategyNonConvex(aStartLocation, collisions, rotA, isCutting);
		}

		runStrategyCollision(aStartLocation, collisions.first(), rotA, isCutting);
	}

	/**
	 * @param aStartLocation
	 * @return
	 */
	protected SortedSet<Collision> diveForCollisions(
			final double[] aStartLocation, final Rotation rotA) {
		SortedSet<Collision> collisions;
		{
		//TODO: support more then just locations in Y+A

		// getCollisions uses a 3D location and direction whereas startLocation
		// is a 4 dimensional machine coordinate including 2 rotational axis

		// direction our cutter is coming from (calculated using A axis)
		Vector3D direction = new Vector3D(0d,0d,-1d);
		

		direction = rotA.applyInverseTo(direction);
//		System.out.println("calling getCollisions machine-location=" + Arrays.toString(aStartLocation));
		
		Vector3D colStartLocation = new Vector3D(aStartLocation[0], aStartLocation[1], aStartLocation[2]);
		colStartLocation = colStartLocation.subtract(direction.scalarMultiply(100));
		collisions = getModel().getCollisions(colStartLocation, direction, mTool, mSkinThickness);
		}
		return collisions;
	}
	/**
	 * No part of the object is below the cutter
	 * @param isCutting true if cutting, false if repositioning (z=free movement height)
	 * @throws IOException 
	 */
     protected void runStrategyHole(final double aStartLocation[], final boolean isCutting) throws IOException {
		System.out.println("hole detected at X" + aStartLocation[0] + " Y" + aStartLocation[1] + " Z" + aStartLocation[2] + " A" + aStartLocation[3]);	
		if (isCutting) {
			aStartLocation[Axis.Z.ordinal()] = mModel.getMinZ(); // cut all the way through
		} else {
			aStartLocation[Axis.Z.ordinal()] = mFreeMovementHeight;
		}
		getNextStrategy().runStrategy(aStartLocation, isCutting);
	}
	/**
	 * Apart from the entry and exit point on a path through the object, there is also a cavity inside.<br/>
	 * This may be a non-nonvex surface that can be milled if cutting from 2 or more directions.
	 * @param aRotA 
	 * @param isCutting true if cutting, false if repositioning (z=free movement height)
	 * @throws IOException 
	 */
	protected void runStrategyNonConvex(final double aStartLocation[], final SortedSet<Collision> aCollisionList, Rotation aRotA, final boolean isCutting) throws IOException {
		//TODO: move Z to travel-height
		//TODO use a linear(X)+linear(Y)+StraightZ combination  along the plane of the start+end of this noncave patch to handle it

		// also cut the topmost collision like in a regular case
		runStrategyCollision(aStartLocation, aCollisionList.first(), aRotA, isCutting);
	}
	/**
	 * The usual case of cutting a point on the (convex) surface.
	 * @param aCollision The point of cutting.
	 * @param aStartLocation the tool coordinates (X,Y,Z, possibly A, B and maybe even C rotational axis too) to reach #aCollision
	 * @param isCutting true if cutting, false if repositioning (z=free movement height)
	 * @param aRotA 
	 */
	protected void runStrategyCollision(final double aStartLocation[], final Collision aCollision, final Rotation aRotA, final boolean isCutting) throws IOException {
		
		if (isCutting) {
			// our collision point refers to a rotated tool. Since actually the part rotates, we need to rotate it back before determining the Z cutting depth
			Vector3D rotatedPoint = aRotA.applyTo(aCollision.getCollisionPoint());
			System.out.println("machine location: " + Arrays.toString(aStartLocation));
			System.out.println("original collision point: " + aCollision.getCollisionPoint().toString());
			System.out.println("rotated  collision point: " + rotatedPoint.toString());
			aStartLocation[Axis.Z.ordinal()] = rotatedPoint.getZ();

			//TEST
			//		aStartLocation[Axis.X.ordinal()] = aCollision.getCollisionPoint().getX();
			//		aStartLocation[Axis.Y.ordinal()] = aCollision.getCollisionPoint().getY();
			//		aStartLocation[Axis.Z.ordinal()] = aCollision.getCollisionPoint().getZ();
			//		aStartLocation[Axis.A.ordinal()] = 0;
		} else {
			// repositioning
			aStartLocation[Axis.Z.ordinal()] = mFreeMovementHeight;
		}
		
		getNextStrategy().runStrategy(aStartLocation, isCutting);
	}


	@Override
	public void endStrategy()  throws IOException {
		// we have nothing to finish/clean up but maybe the next strategy has.
		getNextStrategy().endStrategy();
	}

	public void addProgressListener(final IProgressListener aListener) {
		this.mProgressListener = aListener;
		getNextStrategy().addProgressListener(this);
	}

	@Override
	public void onProgressChanged(IStrategy aSender, long aProgress,
			long aMaximum) {
		this.mProgressListener.onProgressChanged(this, aProgress, aMaximum);
		
	}

	/**
	 * @return Z-position that never collides with any part of the object.
	 */
	public double getFreeMovementHeight() {
		return mFreeMovementHeight;
	}

	/**
	 * @param aFreeMovementHeight Z-position that never collides with any part of the object.
	 */
	public void setFreeMovementHeight(final double aFreeMovementHeight) {
		mFreeMovementHeight = aFreeMovementHeight;
	}

	/**
	 * Leave this much model around the cutter in each direction.
	 * (Used for roughing.)
	 * @return the skinThickness
	 */
	public double getSkinThickness() {
		return mSkinThickness;
	}

	/**
	 * Leave this much model around the cutter in each direction.
	 * (Used for roughing.)
	 * @param aSkinThickness the skinThickness to set
	 */
	public void setSkinThickness(double aSkinThickness) {
		mSkinThickness = aSkinThickness;
	}
}
