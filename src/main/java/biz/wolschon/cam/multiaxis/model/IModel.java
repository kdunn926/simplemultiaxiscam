package biz.wolschon.cam.multiaxis.model;

import java.util.SortedSet;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * A model may use polygons or CSG. This interface *should* be agnostic to that.
 * @author marcuswolschon
 *
 */
public interface IModel {

	double getCenterX();
	double getCenterY();
	double getCenterZ();
	double getMinX();
	double getMinY();
	double getMinZ();
	double getMaxX();
	double getMaxY();
	double getMaxZ();
	double getMin(Axis axis);
	double getMax(Axis axis);
	SortedSet<Collision> getCollisions(Vector3D resolvedLocation, Vector3D direction);
	SortedSet<Collision> getCollisions(Vector3D aLocation, Vector3D aDirection, Tool aTool, double aSkinThickness);
	public int getTriangleCount();
	/**
	 * TODO: abstract from internal representation
	 * @param i
	 * @return 0-2=x,y,z of point1, 3-5=point2, 6-8=point3
	 */
	public Triangle getTriangle(final int i);
	

}
