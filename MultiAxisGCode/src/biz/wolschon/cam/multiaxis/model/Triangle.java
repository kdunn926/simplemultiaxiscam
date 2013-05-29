package biz.wolschon.cam.multiaxis.model;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * A triangle. Used in the model and in rendering.
 * @author marcuswolschon
 *
 */
public class Triangle {

	/**
	 * Normal vector of the triangle.
	 */
	private Vector3D normal;
	/**
	 * Absolute position in 3D space of one point of the triangle.
	 */
	private Vector3D p1;
	/**
	 * Absolute position in 3D space of one point of the triangle.
	 */
	private Vector3D p2;
	/**
	 * Absolute position in 3D space of one point of the triangle.
	 */
	private Vector3D p3;
	/**
	 * The (infinite) plane this triangle creates.
	 * Null until explicitely created.
	 */
	private Plane plane = null;
	/**
	 * 
	 * @param normal Normal vector of the triangle
	 * @param p1 Absolute position in 3D space of one point of the triangle.
	 * @param p2 Absolute position in 3D space of one point of the triangle.
	 * @param p3 Absolute position in 3D space of one point of the triangle.
	 */
	public Triangle(Vector3D normal, Vector3D p1, Vector3D p2, Vector3D p3) {
		this.normal = normal;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	/**
	 * The (infinite) plane this triangle creates.
	 * Create the plane if not already done so.
	 * @return the plane
	 */
	public Plane getPlane() {
		if (plane == null) {
			plane = new Plane(p1, p2, p3);
		}
		return plane;
	}
	/**
	 * @return Normal vector of the triangle.
	 */
	public Vector3D getNormal() {
		return normal;
	}
	/**
	 * @return Absolute position in 3D space of one point of the triangle.
	 */
	public Vector3D getP1() {
		return p1;
	}
	/**
	 * @return Absolute position in 3D space of one point of the triangle.
	 */
	public Vector3D getP2() {
		return p2;
	}
	/**
	 * @return Absolute position in 3D space of one point of the triangle.
	 */
	public Vector3D getP3() {
		return p3;
	}
	/**
	 * @return the hash code to give this triangle a unique number.
	 */
	@Override
	public String toString() {
		return "#" + hashCode();
	}
}
