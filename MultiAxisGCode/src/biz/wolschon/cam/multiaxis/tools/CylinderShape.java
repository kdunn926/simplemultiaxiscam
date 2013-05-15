package biz.wolschon.cam.multiaxis.tools;


/**
 * Cylinder shape (e.g. flat cutter or shaft of any other cutter).
 */
public class CylinderShape implements IToolShape {
	/**
	 * Height of the lowest part of this shape above 0=tip.
	 */
	private double location;

	/**
	 * Diameter of the cylinder shape.
	 */
	private double diameter;

	/**
	 * Length of the cylinder shape.
	 */
	private double length;

	/**
	 * @param location height of the lowest part of this shape above 0=tip.
	 * @param diameter diameter of the cylinder shape
	 * @param length length of the cylinder shape
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
	 * @return Diameter of the cylinder shape
	 */
	public double getDiameter() {
		return diameter;
	}


	/**
	 * @return length of the cylinder shape
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @return Radius  of the cylinder shape
	 */
	public double getRadius() {
		return diameter / 2.0d;
	}
}
