package biz.wolschon.cam.multiaxis.trigonometry;

import java.lang.Math;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import biz.wolschon.cam.multiaxis.tools.BallShape;
import biz.wolschon.cam.multiaxis.tools.ConeShape;
import biz.wolschon.cam.multiaxis.tools.IToolShape;
import biz.wolschon.cam.multiaxis.tools.Tool;

/**
 * Helper functions for trigonometry and inverse kinematics for 4 and 5 axis CAM.
 */
public class Trigonometry {
 	/**
 	 * Helper classes have no public constructor.
 	 */
	private Trigonometry() {
	}

	/**
	 * Calculates the length oft the given vector in 2D space using the given dimensions.
	 * @param aVector the vector to calculate for
	 * @param aFirstAxis the first dimension (e.g. Axis.X)
	 * @param aSecondAxis the second dimension (e.g. Axis.Y)
	 */
	public static double vectorLength2D(final double[] aVector, final Axis aFirstAxis, final Axis aSecondAxis) {
		double d0 = aVector[aFirstAxis.ordinal()] * aVector[aFirstAxis.ordinal()];
		double d1 = aVector[aSecondAxis.ordinal()] * aVector[aSecondAxis.ordinal()];

		double length = Math.sqrt(d0 + d1);
		return length;
	}
	/**
	 * Calculates the length oft the given vector in 2D space using the given dimensions.
	 * @param aVector the vector to use	 
	 * @param aFirstAxis the first dimension (e.g. Axis.X)
	 * @param aSecondAxis the second dimension (e.g. Axis.Y)
	 */
	public static double vectorLength2D(final Vector3D aVector, final Axis aFirstAxis, final Axis aSecondAxis) {
		double d0 = aFirstAxis.get(aVector);
		d0 = d0 * d0;
		double d1 = aSecondAxis.get(aVector);
		d1 = d1 * d1;
		
		double length = Math.sqrt(d0 + d1);
		return length;
	}

	/**
	 * @returns the rotation angle of the given vector around the given axis in Degrees.
	 */
	public static double getRotationAngle(final double[] tool, final Axis rotationAxis) {
		Axis plane[] = rotationAxis.getRotationPlane();
		double length = vectorLength2D(tool, plane[0], plane[1]);
		double normalized[] = new double[] {
				tool[plane[0].ordinal()] / length,
				tool[plane[1].ordinal()] / length
			};
		double angle = Math.atan2(-1.0d * normalized[0], normalized[1]);
		if (angle > Math.PI) {
			angle -= 2.0d * Math.PI;
		}
		if (angle < Math.PI) {
			angle += 2.0d * Math.PI;
		}
		return Math.toDegrees(angle);
	}


	/**
	 * @returns the rotation angle of the given vector around the given axis in Degrees counterclockwise.
	 */
	public static double getRotationAngle(final Vector3D  aVector, final Axis aRotationAxis) {
		Axis plane[] = aRotationAxis.getRotationPlane();

		//double length = vectorLength2D(tool, plane[0], plane[1]);
		//double normalized[] = new double[] {
		//		plane[0].get(tool) / length,
		//		plane[1].get(tool) / length
		//	};

		double angle = Math.atan2(plane[1].get(aVector), plane[0].get(aVector));
		double degrees =  Math.toDegrees(angle) - 90;
		if (degrees >= 360) {
			degrees -= 360;
		}
		if (degrees < 0) {
			degrees += 360;
		}
		return degrees;
	}

	/**
	 * @param anAgle the rotation angle in <b>degrees</b>, counterclockwise.
	 * @returns the rotated 2D vector.
	 */
	public static double[] rotate2D (final double u, final double v, final double anAgle) {
		double rad = Math.toRadians(anAgle);
		double[] retval = new double[] {
			u * Math.cos(rad) - v*Math.sin(rad),
			u * Math.sin(rad) + v*Math.cos(rad)
		};
		return retval;
	}
	/**
	 * Rotate the given location in XZYAB space around A and B axis, so the tool is vertical.
	 * Include an offset introduced due to the tool having a shape and volume<br/>
	 * Side effect: machinePosition is changed to the rotated machine location.
	 */
	public static void inverseToolKinematic5Axis(final double[] aMachinePosition, final Vector3D aToolVector, final Tool aTool) {
		inverseToolKinematic4Axis(aMachinePosition, Axis.A, aToolVector, aTool);
		inverseToolKinematic4Axis(aMachinePosition, Axis.B, aToolVector, aTool);
	}

	/**
	 * Rotate the given location in XZYAB space, so the tool is vertical.
	 * Include an offset introduced due to the tool having a shape and volume<br/>
	 * Side effect: machinePosition is changed to the rotated machine location.
	 */
	public static void inverseToolKinematic4Axis(final double[] aMachinePosition, final Axis aRotationAxis, final Vector3D aToolVector, final Tool aTool) {
		IToolShape tip = aTool.getTipShape();
		//TODO: limit the rotation angle, so no part of the shaft of the tool collides with the part
		if (tip instanceof BallShape) {
			BallShape ball = (BallShape) tip;
			// we rotate the center of the ball, then subtract the radius to get the rotated tip of the ball-tip
			//machinePosition[Axis.Z.ordinal()] -= ball.getRadius();
			inverseKinematic2D(aMachinePosition, aRotationAxis, aToolVector);//, aTool);
			//machinePosition[Axis.Z.ordinal()] += ball.getRadius();
			return;
		}
		if (tip instanceof ConeShape) {
			// no adjustment required since the tip is infinitely small
			// TODO: limit the rotation angle to be at most the angle of the tool
			inverseKinematic2D(aMachinePosition, aRotationAxis, aToolVector);//, aTool);
			return;
		}
		throw new IllegalArgumentException("we only support ball nose and engraving cutters yet, not " + tip.getClass().getName()); //TODO: support more cutter shapes
	}


	/**
	 * Rotate the given location in XZYAB space, so the tool is vertical.<br/>
	 * Side effect: machinePosition is changed to the rotated machine location.<br/>
	 * The tool is assumed to be infinitely small.
	 * @see inverseToolKinematic4Axis
	 */
	protected static void inverseKinematic2D(final double[] aMachinePosition, final Axis aRotationAxis, final Vector3D aTool) {
			//Axis[] plane = aRotationAxis.getRotationPlane();
			//double oldHeight =  aMachinePosition[plane[0].ordinal()];
			//double oldWidth  =  aMachinePosition[plane[1].ordinal()];
			double angle =  Math.toDegrees(Vector3D.angle(aTool, new Vector3D(0, 0, 1)));  //-1 * getRotationAngle(aTool, aRotationAxis);
	System.out.println("inverse kinematic: angle=" + angle + " machinepos(A)=" + aMachinePosition[Axis.A.ordinal()]);

			Vector3D v = new Vector3D(aMachinePosition[0], aMachinePosition[1], aMachinePosition[2]);
			v = aRotationAxis.getRotation(angle).applyTo(v);
			aMachinePosition[0] = v.getX();
			aMachinePosition[1] = v.getY();
			aMachinePosition[2] = v.getZ();
			aMachinePosition[aRotationAxis.ordinal()] = angle;
/*			double rotated[] = rotate2D(oldHeight, oldWidth, angle); //  - aMachinePosition[aRotationAxis.ordinal()]

			aMachinePosition[plane[0].ordinal()] = rotated[0]; 
			aMachinePosition[plane[1].ordinal()] = rotated[1];
			aMachinePosition[aRotationAxis.ordinal()] = angle;*/
			while (aMachinePosition[aRotationAxis.ordinal()] > 360) {
				aMachinePosition[aRotationAxis.ordinal()] -= 360.0d;
			}
			while (aMachinePosition[aRotationAxis.ordinal()] < 0) {
				aMachinePosition[aRotationAxis.ordinal()] += 360.0d;
			}

	}

}
