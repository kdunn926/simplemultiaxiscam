package biz.wolschon.cam.multiaxis.trigonometry;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


/**
 * Possible axis of movement.
 * TODO: A and B axis rotate around the x=y=z=0 origin
 */
public enum Axis {
	X {
		@Override
		public double get(Vector3D v) {
			return v.getX();
		}
	},
	Y {
		@Override
		public double get(Vector3D v) {
			return v.getY();
		}
	},
	Z {
		@Override
		public double get(Vector3D v) {
			return v.getZ();
		}
	},
	/**
	 * The rotation axis A commonly rotates around the X axis and thus in the Z+Y plane.
	 */
	A(X) {
		@Override
		public double get(Vector3D v) {
			throw new IllegalStateException("Vector3D has no A axis");
		}

		@Override
		public Rotation getRotation(double degrees) {
			Rotation rot = new Rotation(new Vector3D(1d,0d,0d), degrees);
			return rot;
		}
	},
	/**
	 * The rotation axis B commonly rotates around the Y axis and thus in the Z+X plane.
	 */
	B(Y) {
		@Override
		public double get(Vector3D v) {
			throw new IllegalStateException("Vector3D has no B axis");
		}

		@Override
		public Rotation getRotation(double degrees) {
			Rotation rot = new Rotation(new Vector3D(0d,1d,0d), degrees);
			return rot;
		}
	},
	/**
	 * The rotation axis C commonly rotates around the Z axis and thus in the X+Y plane.
	 */
	C(Z) {
		@Override
		public double get(Vector3D v) {
			throw new IllegalStateException("Vector3D has no C axis");
		}

		@Override
		public Rotation getRotation(double degrees) {
			Rotation rot = new Rotation(new Vector3D(0d,0d,1d), degrees);
			return rot;
		}
	};
	/**
	 * True if this is a linear and false if this is a rotation axis.
	 * @see #rotatesAround
	 */
	private boolean isLinearAxis;
	/*
	 * @return True if this is a linear and false if this is a rotation axis
	 */
	public boolean isLinearAxis() {
		return isLinearAxis;
	}
	/**
	 * Null for linear axis.
	 * @see #isLinearAxis
	 */
	private Axis rotatesAround;
	/**
	 * Internal constructor for a linear axis.
	 */
	private Axis() {
		isLinearAxis = true;
	}
	/**
	 * Internal constructor for a rotation axis.
	 * @param u see #rotatesAround
	 */
	private Axis(final Axis u) {
		isLinearAxis = false;
		rotatesAround = u;
	}
	/**
	 * It is an error to call this on a linear axis.
	 * @see #isLinearAxis()
	 * @return the axis we rotate around (counterclockwise)
	 */
	public Axis getRotatesAround() {
		if (isLinearAxis) {
			throw new IllegalStateException("call only valid for rotational axis");
		}
		return rotatesAround ;
	}
	/**
	 * It is an error to call this on a linear axis.
	 * @see #isLinearAxis()
	 * @return the plane of rotation (counterclockwise rotation). Always exactly 2 elements.
	 */
	public Axis[] getRotationPlane() {
		if (isLinearAxis) {
			throw new IllegalStateException("call only valid for rotational axis");
		}
		if (rotatesAround == X) {
			return new Axis[] {Y,Z};
		}
		if (rotatesAround == Y) {
			return new Axis[] {X,Z};
		}
		return new Axis[] {X,Y};
	}
	/**
	 * It is an error to call this method on a rotational axis since 3D vectors don't contain that information.
	 * Only machine coordinates to (arrays of double).
	 * @return the element of the given vector that corresponds to this axis.
	 */
	public abstract double get(Vector3D vector3d);
	/**
	 * It is an error to call this method on a linear axis.
	 * @return a rotational matrix for the given rotation
	 * @param degrees how far to rotate counterclockwise in degrees.
	 */
	public Rotation getRotation(double degrees) {
		throw new IllegalStateException("not defined for linear axis");
	}
}
