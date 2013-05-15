package biz.wolschon.cam.multiaxis.tools;


/**
 * Cone shape (e.g. tip of an engraving cutter).
 */
public class ConeShape implements IToolShape {
	/**
	 * Height of the lowest part of this shape above 0=tip.
	 */
	private double location;

	/**
	 * Diameter of the cone shape.
	 */
	private double diameter;

	/**
	 * Length of the cone shape.
	 */
	private double length;


	/**
	 * 
	 * @param location Height of the lowest part of this shape above 0=tip.
	 * @param diameter Diameter of the ball shape.
	 */
	public ConeShape(double location, double diameter, double length) {
		super();
		this.location = location;
		this.diameter = diameter;
		this.length = length;
	}
	/**
	 * @param location height of the lowest part of this shape above 0=tip.
	 * @param diameter diameter of the cone shape
	 * @param length length of the cone shape
	public ConeShape (final double location, final double diameter, final double length) {
		this.location = location;
		this.diameter = diameter;
		this.length   = length;
	}

	/**
	 * @return Height of the lowest part of this shape above 0=tip.
	 */
	public double getLocation() {
		return location;
	}

	/**
	 * @return Diameter of the cone shape
	 */
	public double getDiameter() {
		return diameter;
	}


	/**
	 * @return length of the cone shape
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @return Radius  of the cone shape
	 */
	public double getRadius() {
		return diameter / 2.0d;
	}
}
