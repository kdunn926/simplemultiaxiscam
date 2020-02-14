/**
 * 
 */
package biz.wolschon.cam.multiaxis.trigonometry;

import static org.junit.Assert.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

/**
 * @author marcuswolschon
 *
 */
public class TrigonometryTest {

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#vectorLength2D(double[], biz.wolschon.cam.multiaxis.trigonometry.Axis, biz.wolschon.cam.multiaxis.trigonometry.Axis)}.
	 */
	@Test
	public void testVectorLength2DDoubleArrayAxisAxis() {
//TODO		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#vectorLength2D(org.apache.commons.math3.geometry.euclidean.threed.Vector3D, biz.wolschon.cam.multiaxis.trigonometry.Axis, biz.wolschon.cam.multiaxis.trigonometry.Axis)}.
	 */
	@Test
	public void testVectorLength2DVector3DAxisAxis() {
		//TODO		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#getRotationAngle(double[], biz.wolschon.cam.multiaxis.trigonometry.Axis)}.
	 */
	@Test
	public void testGetRotationAngleDoubleArrayAxis() {
		//TODO		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#getRotationAngle(org.apache.commons.math3.geometry.euclidean.threed.Vector3D, biz.wolschon.cam.multiaxis.trigonometry.Axis)}.
	 */
	@Test
	public void testGetRotationAngleVector3DAxis() {
		double result = Trigonometry.getRotationAngle(new Vector3D(0, 0, 1), Axis.A);
		assertEquals(0,  result, 0.1d);

		result = Trigonometry.getRotationAngle(new Vector3D(0, -1, 0), Axis.A);
		assertEquals(90,  result, 0.1d);

		result = Trigonometry.getRotationAngle(new Vector3D(0, 0, -1), Axis.A);
		assertEquals(180,  result, 0.1d);

		result = Trigonometry.getRotationAngle(new Vector3D(0, -1, 1), Axis.A);
		assertEquals(45,  result, 0.1d);
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#rotate2D(double, double, double)}.
	 */
	@Test
	public void testRotate2D() {
		double[] result = Trigonometry.rotate2D(1, 1, 180);
		assertEquals(-1, result[0], 0.1d);
		assertEquals(-1, result[1], 0.1d);
		
		result = Trigonometry.rotate2D(0, 0, 60);
		assertEquals(0, result[0], 0.1d);
		assertEquals(0, result[1], 0.1d);
		
		result = Trigonometry.rotate2D(0, 1, -90);
		assertEquals(1, result[0], 0.1d);
		assertEquals(0, result[1], 0.1d);
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#inverseToolKinematic5Axis(double[], org.apache.commons.math3.geometry.euclidean.threed.Vector3D, biz.wolschon.cam.multiaxis.tools.Tool)}.
	 */
	@Test
	public void testInverseToolKinematic5Axis() {
		//TODO		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#inverseToolKinematic4Axis(double[], biz.wolschon.cam.multiaxis.trigonometry.Axis, org.apache.commons.math3.geometry.euclidean.threed.Vector3D, biz.wolschon.cam.multiaxis.tools.Tool)}.
	 */
	@Test
	public void testInverseToolKinematic4Axis() {
		//TODO		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link biz.wolschon.cam.multiaxis.trigonometry.Trigonometry#inverseKinematic2D(double[], biz.wolschon.cam.multiaxis.trigonometry.Axis, org.apache.commons.math3.geometry.euclidean.threed.Vector3D)}.
	 */
	@Test
	public void testInverseKinematic2D() {
		//TODO		fail("Not yet implemented");
	}

}
