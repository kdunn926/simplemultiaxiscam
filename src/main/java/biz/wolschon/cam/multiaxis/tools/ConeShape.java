package biz.wolschon.cam.multiaxis.tools;


/**
 * Cone shape (e.g. tip of an engraving cutter).
 */
public class ConeShape implements IToolShape {
	/**
	 * Height of the lowest part of this shape above 0=tip.
	 */
	private double mLocation;

	/**
	 * Diameter of the cone shape.
	 */
	private double mDiameter;

	/**
	 * Length of the cone shape.
	 */
	private double mLength;


	/**
	 * @param aLocation height of the lowest part of this shape above 0=tip.
	 * @param aDiameter diameter of the cone shape
	 * @param aLength length of the cone shape
	 */
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
	 * @return Diameter of the cone shape
	 */
	public double getDiameter() {
		return mDiameter;
	}


	/**
	 * @return length of the cone shape
	 */
	public double getLength() {
		return mLength;
	}

	/**
	 * @return Radius  of the cone shape
	 */
	public double getRadius() {
		return mDiameter / 2.0d;
	}
}
