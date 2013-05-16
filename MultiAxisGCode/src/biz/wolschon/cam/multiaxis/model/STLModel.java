package biz.wolschon.cam.multiaxis.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

import biz.wolschon.cam.multiaxis.trigonometry.Axis;
import biz.wolschon.cam.multiaxis.trigonometry.Trigonometry;

public class STLModel implements IModel {

	public STLModel(File f) throws IOException {
		System.out.println("loading " + f.getAbsolutePath());
		
		FileReader reader = new FileReader(f);
		BufferedReader in = new BufferedReader(reader);
		try {
			String line = in.readLine();
			if (!line.startsWith("solid")) {
				throw new IOException("not an ASCII STL file");
			}

			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("endsolid")) {
					System.out.println("STL: end of first solid. I'm done");
					break;
				}
				String[] tokens = line.split(" ");
				if (tokens.length == 5 && tokens[0].trim().equals("facet") && tokens[1].equals("normal")) {
					readFacet(in, Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));
					continue;
				}
				throw new IOException("not STL: " + line);
			}
			System.out.println("loaded " + this.triangles.size() + " polygons");
			System.out.println("X: " + this.mMinX + " - " + this.mMaxX);
			System.out.println("Y: " + this.mMinY + " - " + this.mMaxY);
			System.out.println("Z: " + this.mMinZ + " - " + this.mMaxZ);
		} finally {
			in.close();
		}
	}
	private java.util.List<Triangle> triangles = new ArrayList<Triangle>();
	private double mMaxX;
	private double mMinX;
	private double mMaxY;
	private double mMinY;
	private double mMaxZ;
	private double mMinZ;

	private void readFacet(BufferedReader in, double normalX,
			double normalY, double normalZ) throws IOException {
		Vector3D normal = new Vector3D(normalX, normalY, normalZ);
		String line = in.readLine().trim();
		if (!line.startsWith("outer loop")) {
			throw new IOException("not an ASCII STL file - no outer loop");
		}
		line = in.readLine().trim();
		String[] tokens = line.split(" ");
		if (tokens.length != 4 || !tokens[0].equals("vertex")) {
			throw new IOException("not an ASCII STL file - no first vertex");
		}
		Vector3D p1 = new Vector3D(Double.parseDouble(tokens[1]),
					Double.parseDouble(tokens[2]),
					Double.parseDouble(tokens[3]));
		line = in.readLine().trim();
		tokens = line.split(" ");
		if (tokens.length != 4 || !tokens[0].equals("vertex")) {
			throw new IOException("not an ASCII STL file - no second vertex");
		}
		Vector3D p2 = new Vector3D(Double.parseDouble(tokens[1]),
				Double.parseDouble(tokens[2]),
				Double.parseDouble(tokens[3]));
		line = in.readLine().trim();
		tokens = line.split(" ");
		if (tokens.length != 4 || !tokens[0].equals("vertex")) {
			throw new IOException("not an ASCII STL file - no third vertex after reading " +this.triangles.size() + " polygons");
		}
		Vector3D p3 = new Vector3D(Double.parseDouble(tokens[1]),
				Double.parseDouble(tokens[2]),
				Double.parseDouble(tokens[3]));
		line = in.readLine().trim();
		if (!line.startsWith("endloop")) {
			throw new IOException("not an ASCII STL file - no endloop");
		}
		line = in.readLine().trim();
		if (!line.startsWith("endfacet")) {
			throw new IOException("not an ASCII STL file - no endfactet");
		}
		addTriangle(new Triangle(normal, p1, p2, p3));
		
		
	}

	private void addTriangle(Triangle triangle) {
		this.mMaxX = Math.max(this.mMaxX, triangle.getP1().getX());
		this.mMaxX = Math.max(this.mMaxX, triangle.getP2().getX());
		this.mMaxX = Math.max(this.mMaxX, triangle.getP3().getX());

		this.mMinX = Math.min(this.mMinX, triangle.getP1().getX());
		this.mMinX = Math.min(this.mMinX, triangle.getP2().getX());
		this.mMinX = Math.min(this.mMinX, triangle.getP3().getX());


		this.mMaxY = Math.max(this.mMaxY, triangle.getP1().getY());
		this.mMaxY = Math.max(this.mMaxY, triangle.getP2().getY());
		this.mMaxY = Math.max(this.mMaxY, triangle.getP3().getY());

		this.mMinY = Math.min(this.mMinY, triangle.getP1().getY());
		this.mMinY = Math.min(this.mMinY, triangle.getP2().getY());
		this.mMinY = Math.min(this.mMinY, triangle.getP3().getY());


		this.mMaxZ = Math.max(this.mMaxZ, triangle.getP1().getZ());
		this.mMaxZ = Math.max(this.mMaxZ, triangle.getP2().getZ());
		this.mMaxZ = Math.max(this.mMaxZ, triangle.getP3().getZ());

		this.mMinZ = Math.min(this.mMinZ, triangle.getP1().getZ());
		this.mMinZ = Math.min(this.mMinZ, triangle.getP2().getZ());
		this.mMinZ = Math.min(this.mMinZ, triangle.getP3().getZ());
		

		this.triangles.add(triangle);
	}

	@Override
	public double getCenterX() {
		return getMinX() + (getMaxX() - getMinX())/2.0d;
	}

	@Override
	public double getCenterY() {
		return getMinY() + (getMaxY() - getMinY())/2.0d;
	}

	@Override
	public double getCenterZ() {
		return getMinZ() + (getMaxZ() - getMinZ())/2.0d;
	}

	@Override
	public double getMinX() {
		return mMinX;
	}

	@Override
	public double getMinY() {
		return mMinY;
	}

	@Override
	public double getMinZ() {
		return mMinZ;
	}

	@Override
	public double getMaxX() {
		return mMaxX;
	}

	@Override
	public double getMaxY() {
		return mMaxY;
	}

	@Override
	public double getMaxZ() {
		return mMaxZ;
	}
	@Override
	public double getMax(final Axis axis) {
		switch (axis) {
		case X: return getMaxX();
		case Y: return getMaxY();
		case Z: return getMaxZ();
		case A: return 360;
		case B: return 360;
		default: throw new IllegalArgumentException("only X,Y, Z and A/B axis supported");
		}
	}
	@Override
	public double getMin(final Axis axis) {
		switch (axis) {
		case X: return getMinX();
		case Y: return getMinY();
		case Z: return getMinZ();
		case A: return 0;
		case B: return 0;
		default: throw new IllegalArgumentException("only X,Y, Z and A/B axis supported");
		}
	}

	/**
	 * return the ID (index) or all triangles that are intersected by a ray
	 * starting in location and racing of into direction.
	 */
	@Override
	public SortedSet<Collision> getCollisions(final Vector3D aLocation, final Vector3D aDirection) {
		SortedSet<Collision> result = new TreeSet<Collision>(new Comparator<Collision>() {

			@Override
			public int compare(Collision first, Collision second) {
				double length1 = first.getCollisionPoint().subtract(aLocation).getNorm();
				double length2 = second.getCollisionPoint().subtract(aLocation).getNorm();
				if (length2 > length1) {
					return 1;
				}
				if (length2 < length1) {
					return 2;
				}
				return 0;
			}
		});
		for (Triangle triangle : this.triangles) {
			Collision c = checkForCollision(triangle, aLocation, aDirection);
			if (c != null) {
				result.add(c);
			}
		}
		return result;
	}

	
	protected static Collision checkForCollision(Triangle triangle, Vector3D location,
			Vector3D direction) {

		Vector3D a = triangle.getP1();
		Vector3D b = triangle.getP2();
		Vector3D c = triangle.getP3();
		
		Plane plane = triangle.getPlane();
		Vector3D p = plane.intersection(new Line(location, location.add(direction)));
		if (p == null) {
			return null; // parallel to plane
		}

		//--------------------
		// from http://www.blackpawn.com/texts/pointinpoly/default.html

		// Compute vectors        
		Vector3D v0 = c.subtract(a);
		Vector3D v1 = b.subtract(a);
		Vector3D v2 = p.subtract(a);

		// Compute dot products
		double dot00 = v0.dotProduct(v0);
		double dot01 = v0.dotProduct(v1);
		double dot02 = v0.dotProduct(v2);
		double dot11 = v1.dotProduct(v1);
		double dot12 = v1.dotProduct(v2);

		// Compute barycentric coordinates
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		boolean inTriangle = (u >= 0) && (v >= 0) && (u + v <= 1);
		if (!inTriangle) {
			return null;
		}
		return new Collision(triangle, p, u, v);
	}

	@Override
	public int getTriangleCount() {
		return triangles.size();
	}
	@Override
	public Triangle getTriangle(final int i) {
		return this.triangles.get(i);
	}
}
