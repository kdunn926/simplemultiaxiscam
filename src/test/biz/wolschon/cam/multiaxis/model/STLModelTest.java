package biz.wolschon.cam.multiaxis.model;

import static org.junit.Assert.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

public class STLModelTest {

	@Test
	public void testCheckForCollisionEdge() {
		Vector3D direction = new Vector3D(0, 0, -1);
		Vector3D location = new Vector3D(0, 0, 1);
		Triangle triangle = new Triangle(new Vector3D(0, 0, 1),
											new Vector3D(1, 1, 0),
											new Vector3D(0, 0, 0),
											new Vector3D(0, 1, 0));
		Collision c = STLModel.checkForCollision(triangle, location, direction, null, 0.0d);
		assertNotNull(c);
		assertEquals(0, c.getCollisionPoint().getX(), 0.0001d);
		assertEquals(0, c.getCollisionPoint().getY(), 0.0001d);
		assertEquals(0, c.getCollisionPoint().getZ(), 0.0001d);
	}
	@Test
	public void testCheckForCollisionCenter() {
		Vector3D direction = new Vector3D(0, 0, -1);
		Vector3D location = new Vector3D(0.5, 0.75, 1);
		Triangle triangle = new Triangle(new Vector3D(0, 0, 1),
											new Vector3D(1, 1, 0),
											new Vector3D(0, 0, 0),
											new Vector3D(0, 1, 0));
		Collision c = STLModel.checkForCollision(triangle, location, direction, null, 0.0d);
		assertNotNull(c);
		assertEquals(0.5, c.getCollisionPoint().getX(), 0.0001d);
		assertEquals(0.75, c.getCollisionPoint().getY(), 0.0001d);
		assertEquals(0, c.getCollisionPoint().getZ(), 0.0001d);
	}

	@Test
	public void testCheckForNoCollision() {
		Vector3D direction = new Vector3D(0, 0, -1);
		Vector3D location = new Vector3D(0.5, 1.75, 1);
		Triangle triangle = new Triangle(new Vector3D(0, 0, 1),
											new Vector3D(1, 1, 0),
											new Vector3D(0, 0, 0),
											new Vector3D(0, 1, 0));
		Collision c = STLModel.checkForCollision(triangle, location, direction, null, 0.0d);
		assertNull(c);
	}

}
