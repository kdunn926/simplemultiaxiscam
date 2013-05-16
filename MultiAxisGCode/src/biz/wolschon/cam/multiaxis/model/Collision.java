package biz.wolschon.cam.multiaxis.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Collision {

	private Triangle triangle;
	private Vector3D collisionPoint;
	private double u;
	private double v;
	protected Collision(final Triangle triangle, final Vector3D p, final double u, final double v) {
		this.triangle = triangle;
		this.collisionPoint = p;
		this.u = u;
		this.v = v;
	}
	public Vector3D getCollisionPoint() {
		return collisionPoint;
	}
	public Triangle getCollidingPolygon() {
		return triangle;
	}
}
