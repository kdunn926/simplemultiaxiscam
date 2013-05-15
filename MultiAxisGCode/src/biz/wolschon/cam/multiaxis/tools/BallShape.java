package biz.wolschon.cam.multiaxis.tools;


/**
 * Ball shape (e.g. tip of a ball-node cutter).
 */
public class BallShape implements IToolShape {
	/**
	 * Height of the lowest part of this shape above 0=tip.
	 */
	private double location;

	/**
	 * Diameter of the ball shape.
	 */
	private double diameter;

	/**
	 * 
	 * @param location Height of the lowest part of this shape above 0=tip.
	 * @param diameter Diameter of the ball shape.
	 */
	public BallShape(double location, double diameter) {
		super();
		this.location = location;
		this.diameter = diameter;
	}

	/**
	 * @param location height of the lowest part of this shape above 0=tip.
	 * @param diameter of the ball shape
	public BallShape (final double location, final double diameter) {
		this.location = location;
		this.diameter = diameter;
	}

	/**
	 * @return Height of the lowest part of this shape above 0=tip.
	 */
	public double getLocation() {
		return location;
	}

	/**
	 * @return Diameter of the ball shape
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * @return Radius of the ball shape
	 */
	public double getRadius() {
		return diameter / 2.0d;
	}
}
