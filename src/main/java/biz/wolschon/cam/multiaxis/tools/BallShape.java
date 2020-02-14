package biz.wolschon.cam.multiaxis.tools;


/**
 * Ball shape (e.g. tip of a ball-node cutter).
 */
public class BallShape implements IToolShape {
	/**
	 * Height of the lowest part of this shape above 0=tip.
	 */
	private double mLocation;

	/**
	 * Diameter of the ball shape.
	 */
	private double mDiameter;

	/**
	 * 
	 * @param aLocation Height of the lowest part of this shape above 0=tip.
	 * @param aDiameter Diameter of the ball shape.
	 */
	public BallShape(double aLocation, double aDiameter) {
		super();
		this.mLocation = aLocation;
		this.mDiameter = aDiameter;
	}

	/**
	 * @param aLocation height of the lowest part of this shape above 0=tip.
	 * @param aDiameter of the ball shape
	public BallShape (final double aLocation, final double aDiameter) {
		this.mLocation = aLocation;
		this.mDiameter = aDiameter;
	}

	/**
	 * @return Height of the lowest part of this shape above 0=tip.
	 */
	public double getLocation() {
		return mLocation;
	}

	/**
	 * @return Diameter of the ball shape
	 */
	public double getDiameter() {
		return mDiameter;
	}

	/**
	 * @return Radius of the ball shape
	 */
	public double getRadius() {
		return mDiameter / 2.0d;
	}
}
