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
	private IProgressListener mProgressListener;

	/**
	 * @param aTool The tool we are cutting with. (to determine collisions of tool and part)
	 * @param aModel The part we are trying to mill. (to detemine the Z depth to cut)
	 * @param aNext The next strategy to call
	 */
	public StraightZCutStrategy(IModel aModel, IStrategy aNext, final Tool aTool) {
		this.mModel = aModel;
		this.mNext = aNext;
		this.mTool  = aTool;
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
	 * (honouring values for A and B axis in the location if such rotations have been set by a previous strategy)
	 * and calls #runStrategyHole (nothing below the cutter)
	 * #runStrategyNonConvex (more then 2 intersections with the object)
	 * or #runStrategyCollision
	 */
	@Override
	public void runStrategy(final double aStartLocation[]) throws IOException {
		//TODO: support more then just locations in Y+A

		// getCollisions uses a 3D location and direction whereas startLocation
		// is a 4 dimensional machine coordinate including 2 rotational axis

		// direction our cutter is coming from (calculated using A axis)
		Vector3D direction = new Vector3D(0d,0d,-1d);
		Rotation rotA = Axis.A.getRotation(aStartLocation[Axis.A.ordinal()]);

		direction = rotA.applyInverseTo(direction);
//		System.out.println("calling getCollisions machine-location=" + Arrays.toString(aStartLocation));
		
		Vector3D colStartLocation = new Vector3D(aStartLocation[0], aStartLocation[1], aStartLocation[2]);
		colStartLocation = colStartLocation.subtract(direction.scalarMultiply(100));//TODO: test
		SortedSet<Collision> collisions = getModel().getCollisions(colStartLocation, direction);//, mTool);
		if (collisions.size() == 0) {
			runStrategyHole(aStartLocation);
			return; // TODO: cut all the way through (hole)
		}
		if (collisions.size() > 2) {
			// we detected a non-convex patch
			runStrategyNonConvex(aStartLocation, collisions, rotA);
		}
		//TODO: sort collisions by distance

		runStrategyCollision(aStartLocation, collisions.first(), rotA);
	}
	/**
	 * No part of the object is below the cutter
	 * @throws IOException 
	 */
     protected void runStrategyHole(final double aStartLocation[]) throws IOException {
		System.out.println("hole detected at X" + aStartLocation[0] + " Y" + aStartLocation[1] + " Z" + aStartLocation[2] + " A" + aStartLocation[3]);	
		aStartLocation[Axis.Z.ordinal()] = mModel.getMinZ(); // cut all the way through
		getNextStrategy().runStrategy(aStartLocation);
	}
	/**
	 * Apart from the entry and exit point on a path through the object, there is also a cavity inside.<br/>
	 * This may be a non-nonvex surface that can be milled if cutting from 2 or more directions.
	 * @param aRotA 
	 * @throws IOException 
	 */
	protected void runStrategyNonConvex(final double aStartLocation[], final SortedSet<Collision> aCollisionList, Rotation aRotA) throws IOException {
		//TODO: move Z to travel-height
		//TODO use a linear(X)+linear(Y)+StraightZ combination  along the plane of the start+end of this noncave patch to handle it

		// also cut the topmost collision like in a regular case
		runStrategyCollision(aStartLocation, aCollisionList.first(), aRotA);
	}
	/**
	 * The usual case of cutting a point on the (convex) surface.
	 * @param aCollision The point of cutting.
	 * @param aStartLocation the tool coordinates (X,Y,Z, possibly A, B and maybe even C rotational axis too) to reach #aCollision
	 * @param aRotA 
	 */
	protected void runStrategyCollision(final double aStartLocation[], final Collision aCollision, final Rotation aRotA) throws IOException {
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
		
		getNextStrategy().runStrategy(aStartLocation);
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
}
