package biz.wolschon.cam.multiaxis.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import biz.wolschon.cam.multiaxis.tools.BallShape;
import biz.wolschon.cam.multiaxis.tools.IToolShape;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

public class STLModel implements IModel {
    /**
     * Size of a binary STL header.
     */
    private final static int STL_HEADER_SIZE = 80;


	public STLModel(File f) throws IOException {
		System.out.println("loading " + f.getAbsolutePath());
		
		FileReader reader = new FileReader(f);
		BufferedReader in = new BufferedReader(reader);
		try {
			String line = in.readLine();
			if (!line.startsWith("solid")) {
				//throw new IOException("not an ASCII STL file");
				System.out.println("not an ASCII STL file, trying binary");
				reader.close();
				in.close();
				in = null;
				loadBinarySTL(f);

				System.out.println("loaded (binary) " + this.triangles.size() + " polygons");
				System.out.println("X: " + this.mMinX + " - " + this.mMaxX);
				System.out.println("Y: " + this.mMinY + " - " + this.mMaxY);
				System.out.println("Z: " + this.mMinZ + " - " + this.mMaxZ);
				return;
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
			if (in != null) {
				in.close();
			}
		}
	}

	private void loadBinarySTL(File f) throws FileNotFoundException, IOException {
		DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
		long skipped = din.skip(STL_HEADER_SIZE);
		if (skipped != STL_HEADER_SIZE) {
			System.err.println("skipped only " + skipped + " byte of header!!!");
			}
		int numFaces = read4Byte(din);
		System.out.println("polygon count in binary STL:" + numFaces);
		int expectedSize = numFaces * (12*4)+4+STL_HEADER_SIZE;
		if (f.length() != expectedSize) {
			System.err.print("file size is: " + f.length() + " expected:" + expectedSize);
		} else {
			System.out.println("file size matches our expectation");
		}
		for (int face = 0; face < numFaces; face++) {
			System.out.print(".");
			Vector3D normal = new Vector3D(Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)));
			Vector3D p1 = new Vector3D(Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)));
			Vector3D p2 = new Vector3D(Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)));
			Vector3D p3 = new Vector3D(Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)),
					Float.intBitsToFloat(read4Byte(din)));
		    addTriangle(new Triangle(normal, p1, p2, p3));
		    // skip padding
		    int attrCountByte0 = din.read();
		    if (attrCountByte0 != 0) {
		    	throw new IllegalArgumentException("Attribute byte count " + attrCountByte0 + " != 0 not supported");
		    }
		    int attrCountByte1 = din.read();
		    if (attrCountByte1 != 0) {
		    	throw new IllegalArgumentException("Attribute byte count " + attrCountByte1 + "<<8 != 0 not supported");
		    }
		    
		}
	    din.close();
	}
	
	private int read4Byte(DataInputStream aDin) throws IOException {
		byte[] buf = new byte[4];
		if (aDin.read(buf, 0, 4) != 4) {
			throw new IOException("could not read 4 byte");
		}
		return bufferToInt(buf);
	}


    private final int bufferToInt(byte[] buf) {
        return byteToInt(buf[0]) | (byteToInt(buf[1]) << 8)
                | (byteToInt(buf[2]) << 16) | (byteToInt(buf[3]) << 24);
    }

    private final int byteToInt(byte b) {
        return (b < 0 ? 256 + b : b);
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
		return getCollisions(aLocation, aDirection, null);
	}
	/**
	 * return the ID (index) or all triangles that are intersected by a ray
	 * starting in location and racing of into direction.
	 */
	public SortedSet<Collision> getCollisions(final Vector3D aLocation, final Vector3D aDirection, final Tool aTool) {
	
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
//TODO: optimize to not test all triangles
//TODO: paralellize this to multiple processors
		for (Triangle triangle : this.triangles) {
			Collision c = checkForCollision(triangle, aLocation, aDirection, aTool);
			if (c != null) {
				result.add(c);
			}
		}
		return result;
	}

	private static boolean sameSide(final Vector3D p1, final Vector3D p2, final Vector3D a, final Vector3D b) {
		Vector3D cp1 = b.subtract(a).crossProduct(p1.subtract(a));
		Vector3D cp2 = b.subtract(a).crossProduct(p2.subtract(a));
		return (cp1.dotProduct(cp2) >= 0.0d);
	}

	/**
	 * Check if and at what depth the tool collides with a given triangle
	 * @param aTool may be null. If not null the actual shape of the tool will be used
	 * @return null or the details of the collision
	 */
	protected static Collision checkForCollision(Triangle aTriangle, Vector3D sPosition,
			Vector3D sDir, final Tool aTool) {
		//TODO: rewrite this to use double variables instead of Vector3D to cut down on object allocation and garbage collection
		//TODO: do the hit-test in FLOAT and if positive, calculate again to have the hit-point as DOUBLE

		Vector3D pOrigin = aTriangle.getP1(); // this vector may be offset from the real world coordinates
		Vector3D pNormal = aTriangle.getNormal();
		double hitPointOffset = 0;
		if (aTool != null) {
			IToolShape shape = aTool.getTipShape();
			if (shape instanceof BallShape) {
				BallShape ball = (BallShape) shape;
				// move the tool up to compensate for the coordinate being of the tip and not the center of the ball
				hitPointOffset = ball.getDiameter() / 2.0d;
				// "move all triangles up" along sDir to compensate for the ball radius
				pOrigin = pOrigin.add(pNormal.scalarMultiply(hitPointOffset));
			}
			// nothing
		}
		

		double d = -1.0d * (pNormal.dotProduct(pOrigin));
		double num = pNormal.dotProduct(sPosition) + d;
		double denom = pNormal.dotProduct(sDir);
		
		double hitPoint =  -1.0d * (num / denom);
		if (hitPoint < 0 || Double.isInfinite(hitPoint) || Double.isNaN(hitPoint)) {
			return null;
		}
		Vector3D sDirMult = sDir.scalarMultiply(hitPoint);
		if (sDirMult.isInfinite() || sDirMult.isNaN()) {
			throw new IllegalArgumentException("One coordinate of the collision point is NaN or Infinity");
		}
		
		Vector3D p = sPosition.add(sDirMult);
		if (p.isInfinite() || p.isNaN()) {
			throw new IllegalArgumentException("One coordinate of the collision point is NaN or Infinity");
		}
		
		if (hitPointOffset > 0) {
			p = p.subtract(pNormal.scalarMultiply(hitPointOffset));
			if (p.isInfinite() || p.isNaN()) {
				throw new IllegalArgumentException("One coordinate of the collision point is NaN or Infinity");
			}
			
		}
		//--------------------
		// is the hit point inside the triangle and not just instide the plane?
		// from http://www.blackpawn.com/texts/pointinpoly/default.html

		//return isInside0(p, aTriangle);

		/*if (isInside1(p, aTriangle)) {
			double  u = 0;
			double  v = 0; //TODO: test this algorithm and remove u and v if it works
			return new Collision(aTriangle, p, u, v);
		}
		return null;*/

		return isInside2(p, aTriangle);
	}

	private static Collision isInside0(final Vector3D p, final Triangle aTriangle) {
		// Compute vectors        
		Vector3D v0 = aTriangle.getP3().subtract(aTriangle.getP1());
		Vector3D v1 = aTriangle.getP2().subtract(aTriangle.getP1());
		Vector3D v2 = p.subtract(aTriangle.getP1());

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
		return new Collision(aTriangle, p, u, v);
	}

	private static boolean isInside1(final Vector3D p, final Triangle aTriangle) {
		if (!sameSide(p, aTriangle.getP1(), aTriangle.getP2(), aTriangle.getP3())) {
			return false;
		}
		if (!sameSide(p, aTriangle.getP2(), aTriangle.getP1(), aTriangle.getP3())) {
			return false;
		}
		if (!sameSide(p, aTriangle.getP3(), aTriangle.getP2(), aTriangle.getP1())) {
			return false;
		}
		
		return true;
	}
	private static Collision isInside2(final Vector3D p, final Triangle aTriangle) {
		//TODO: optimize this to reduce object creation
		//TODO: remove u and v as output and change everything in here to float (good enough for a hit-test)
		Vector3D v0 = aTriangle.getP3().subtract(aTriangle.getP1());
		Vector3D v1 = aTriangle.getP2().subtract(aTriangle.getP1());
		Vector3D v2 = p.subtract(aTriangle.getP1());

		double dot00 = v0.dotProduct(v0);
		double dot01 = v0.dotProduct(v1);
		double dot02 = v0.dotProduct(v2);
		double dot11 = v1.dotProduct(v1);
		double dot12 = v1.dotProduct(v2);

		double invD = 1.0d / (dot00 * dot11 - dot01 * dot01);

		// Barycentric coordinates
		double u = (dot11 * dot02 - dot01 * dot12) * invD;
		double v = (dot00 * dot12 - dot01 * dot02) * invD;

		if (u <= 0.0d || v <= 0.0d || u+v >= 1.0d) {
			return null;
		}
		return new Collision(aTriangle, p, u, v);

	}
	/*
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
*/

	@Override
	public int getTriangleCount() {
		return triangles.size();
	}
	@Override
	public Triangle getTriangle(final int i) {
		return this.triangles.get(i);
	}
}
