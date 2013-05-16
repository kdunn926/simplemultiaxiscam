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
	 * @param vector the vector to use	 
	 * @param first the first dimension (e.g. Axis.X)
	 * @param second the second dimension (e.g. Axis.Y)
	 */
	public static double vectorLength2D(final double[] vector, final Axis first, final Axis second) {
		double d0 = vector[first.ordinal()] * vector[first.ordinal()];
		double d1 = vector[second.ordinal()] * vector[second.ordinal()];

		double length = Math.sqrt(d0 + d1);
		return length;
	}
	/**
	 * Calculates the length oft the given vector in 2D space using the given dimensions.
	 * @param vector the vector to use	 
	 * @param first the first dimension (e.g. Axis.X)
	 * @param second the second dimension (e.g. Axis.Y)
	 */
	public static double vectorLength2D(final Vector3D vector, final Axis first, final Axis second) {
		double d0 = first.get(vector);
		d0 = d0 * d0;
		double d1 = second.get(vector);
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
	public static double getRotationAngle(final Vector3D  tool, final Axis rotationAxis) {
		Axis plane[] = rotationAxis.getRotationPlane();
		//double length = vectorLength2D(tool, plane[0], plane[1]);
		//double normalized[] = new double[] {
		//		plane[0].get(tool) / length,
		//		plane[1].get(tool) / length
		//	};
		double angle = Math.atan2(plane[1].get(tool), plane[0].get(tool));
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
	 * @param angle the rotation angle in degrees, counterclockwise.
	 * @returns the rotated 2D vector.
	 */
	public static double[] rotate2D (final double u, final double v, final double angle) {
		double rad = Math.toRadians(angle);
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
	public static void inverseToolKinematic5Axis(final double[] machinePosition, final Vector3D toolVector, final Tool tool) {
		inverseToolKinematic4Axis(machinePosition, Axis.A, toolVector, tool);
		inverseToolKinematic4Axis(machinePosition, Axis.B, toolVector, tool);
	}

	/**
	 * Rotate the given location in XZYAB space, so the tool is vertical.
	 * Include an offset introduced due to the tool having a shape and volume<br/>
	 * Side effect: machinePosition is changed to the rotated machine location.
	 */
	public static void inverseToolKinematic4Axis(final double[] machinePosition, final Axis rotationAxis, final Vector3D toolVector, final Tool tool) {
		IToolShape tip = tool.getTipShape();
		//TODO: limit the rotation angle, so no part of the shaft of the tool collides with the part
		if (tip instanceof BallShape) {
			BallShape ball = (BallShape) tip;
			// we rotate the center of the ball, then subtract the radius to get the rotated tip of the ball-tip
			//machinePosition[Axis.Z.ordinal()] -= ball.getRadius();
			inverseKinematic2D(machinePosition, rotationAxis, toolVector);
			//machinePosition[Axis.Z.ordinal()] += ball.getRadius();
			return;
		}
		if (tip instanceof ConeShape) {
			// no adjustment required since the tip is infinitely small
			// TODO: limit the rotation angle to be at most the angle of the tool
			inverseKinematic2D(machinePosition, rotationAxis, toolVector);
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
	protected static void inverseKinematic2D(final double[] machinePosition, final Axis rotationAxis, final Vector3D tool) {
			Axis[] plane = rotationAxis.getRotationPlane();
			double oldHeight =  machinePosition[plane[0].ordinal()];
			double oldWidth =  machinePosition[plane[1].ordinal()];
			double angle = -1 * getRotationAngle(tool, rotationAxis);
			double rotated[] = rotate2D(oldHeight, oldWidth, angle); //  - machinePosition[rotationAxis.ordinal()]
			machinePosition[plane[0].ordinal()] = rotated[0]; 
			machinePosition[plane[1].ordinal()] = rotated[1];
			machinePosition[rotationAxis.ordinal()] = angle;
			while (machinePosition[rotationAxis.ordinal()] > 360) {
				machinePosition[rotationAxis.ordinal()] -= 360.0d;
			}
			while (machinePosition[rotationAxis.ordinal()] < 0) {
				machinePosition[rotationAxis.ordinal()] += 360.0d;
			}

	}

}
