package biz.wolschon.cam.multiaxis.model;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Triangle {

	private Vector3D normal;
	private Vector3D p1;
	private Vector3D p2;
	private Vector3D p3;
	private Plane plane = null;
	public Triangle(Vector3D normal, Vector3D p1, Vector3D p2, Vector3D p3) {
		this.normal = normal;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	public Plane getPlane() {
		if (plane == null) {
			plane = new Plane(p1, p2, p3);
		}
		return plane;
	}
	public Vector3D getNormal() {
		return normal;
	}
	public Vector3D getP1() {
		return p1;
	}
	public Vector3D getP2() {
		return p2;
	}
	public Vector3D getP3() {
		return p3;
	}
}
