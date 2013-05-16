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
	private boolean isLinearAxis;
	public boolean isLinearAxis() {
		return isLinearAxis;
	}
	private Axis 	rotatesAround;
	private Axis() {
		isLinearAxis = true;
	}
	private Axis(final Axis u) {
		isLinearAxis = false;
		rotatesAround = u;
	}
	public Axis getRotatesAround() {
		if (isLinearAxis) {
			throw new IllegalStateException("call only valid for rotational axis");
		}
		return rotatesAround ;
	}
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
	public abstract double get(Vector3D vector3d);
	public Rotation getRotation(double degrees) {
		throw new IllegalStateException("not defined for linear axis");
	}
}
