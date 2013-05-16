package biz.wolschon.cam.multiaxis.tools;


/**
 * Cylinder shape (e.g. flat cutter or shaft of any other cutter).
 */
public class CylinderShape implements IToolShape {
	/**
	 * Height of the lowest part of this shape above 0=tip.
	 */
	private double mLocation;

	/**
	 * Diameter of the cylinder shape.
	 */
	private double mDiameter;

	/**
	 * Length of the cylinder shape.
	 */
	private double mLength;

	/**
	 * @param aLocation height of the lowest part of this shape above 0=tip.
	 * @param aDiameter diameter of the cylinder shape
	 * @param aLength length of the cylinder shape
	public ConeShape (final double aLocation, final double aDiameter, final double aLength) {
		this.mLocation = aLocation;
		this.mDiameter = aDiameter;
		this.mLength   = aLength;
	}

	/**
	 * @return Height of the lowest part of this shape above 0=tip.
	 */
	public double getLocation() {
		return mLocation;
	}

	/**
	 * @return Diameter of the cylinder shape
	 */
	public double getDiameter() {
		return mDiameter;
	}


	/**
	 * @return length of the cylinder shape
	 */
	public double getLength() {
		return mLength;
	}

	/**
	 * @return Radius  of the cylinder shape
	 */
	public double getRadius() {
		return mDiameter / 2.0d;
	}
}
