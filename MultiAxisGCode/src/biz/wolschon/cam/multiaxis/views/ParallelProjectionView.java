package biz.wolschon.cam.multiaxis.views;


import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.model.Triangle;
import biz.wolschon.cam.multiaxis.tools.BallShape;
import biz.wolschon.cam.multiaxis.tools.IToolShape;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;
import biz.wolschon.cam.multiaxis.trigonometry.Trigonometry;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.JPanel;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Panel that shows a parallel projection of a model and a tool.
 */
public class ParallelProjectionView extends JPanel {
	/**
	 * The model we display.
	 */
	private IModel mModel;
	/**
	 * Tool to draw on top of the model<br/>
	 * <b>May be null</b>.
	 */
	private Tool   mTool;
	/**
	 * X,Y,Z,A,B location where to draw our #mTool (if both are != null)<br/>
	 * <b>May be null</b>.
	 */
	private double[]  mToolLocation;

	private Axis mAxisVertical = Axis.Z;
	private double mVerticalMin;
	private double mVerticalMax;

	private Axis mAxisHorizontal;
	private double mHorizontalMin;
	private double mHorizontalMax;

	/**
	 * Bitmap used for double buffering.
	 */
	private BufferedImage mDoubleBuffer;
	/**
	 * Used for double buffering.
	 */
	private static final GraphicsConfiguration mGraphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	public ParallelProjectionView(final IModel model, final Tool tool) {
		this.mModel = model;
		this.mTool  = tool;
		setVerticalAxis(Axis.Z);
		setHorizontalAxis(Axis.X);
	}

	public void setToolLocation(final double[] toolLocation) {
		this.mToolLocation = toolLocation;
		this.mDoubleBuffer = null;
		repaint();
	}

	public void setVerticalAxis(final Axis a) {
		if (this.mAxisVertical == a)  {
			return;
		}
		this.mAxisVertical = a;
		this.mVerticalMin = mModel.getMin(this.mAxisVertical);
		this.mVerticalMax = mModel.getMax(this.mAxisVertical);	
		this.mDoubleBuffer = null;
		repaint();	
	}

	public void setHorizontalAxis(final Axis a) {
		if (this.mAxisHorizontal == a)  {
			return;
		}
		this.mAxisHorizontal = a;
		this.mHorizontalMin = mModel.getMin(this.mAxisHorizontal);
		this.mHorizontalMax = mModel.getMax(this.mAxisHorizontal);		
		this.mDoubleBuffer = null;
		repaint();
	}

	/**
	 * Draw #mDoubleBuffer to the screen. Call #paintToBuffer(Graphics) with a new buffer if it is null or no longer matches our size on screen.
	 */
	
	@Override
	public void paintAll(Graphics g) {
		if (mDoubleBuffer == null ||
		    mDoubleBuffer.getWidth() != getWidth() ||
		    mDoubleBuffer.getHeight() != getHeight()) {
			mDoubleBuffer = mGraphicsConf.createCompatibleImage(getWidth(), getHeight());
			paintToBuffer((Graphics2D) mDoubleBuffer.getGraphics());
		}
		g.drawImage(mDoubleBuffer, 0, 0, this);
	}

	/**
	 * Do the actual painting. Called by #paintcomponent(Graphics);
	 */
	private void paintToBuffer(final Graphics2D g) {
		// fill background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw mesh
		double horizontalScale = getWidth() * (mHorizontalMax - mHorizontalMin);
		double verticalScale = getWidth() * (mHorizontalMax - mHorizontalMin);
		double scale = Math.min(horizontalScale, verticalScale);
		//TODO: draw mesh
		int count = mModel.getTriangleCount();
		g.setColor(Color.GRAY);
		for(int i=0; i<count; i++) {
			// project the 3 vertices of the triangle onto the screen
			// the model always has A=0 and B=0 coordinates
			Triangle triangle = mModel.getTriangle(i);
			int[] project0 = projectPoint3D(triangle.getP1(), scale);
			int[] project1 = projectPoint3D(triangle.getP2(), scale);
			int[] project2 = projectPoint3D(triangle.getP3(), scale);

			//TODO: allow solid in addition to wireframe
			g.drawLine(project0[0], project0[1], project1[0], project1[1]);
			g.drawLine(project1[0], project1[1], project2[0], project2[1]);
			g.drawLine(project2[0], project2[1], project0[0], project0[1]);
		}

		// draw tool
		if (mTool != null && mToolLocation != null) {
			g.setColor(Color.RED);
			for (IToolShape shape : mTool.getShape()) {
				// draw tool on top of wireframe
				if (shape instanceof BallShape) {
					BallShape ball = (BallShape) shape;
					int[] center = projectPoint5D(mToolLocation, ball.getLocation()+ball.getRadius(), 0.0d);
					//TODO: allow solid in addition to wireframe
					int d = (int) ( ball.getDiameter() * scale);
					g.drawOval(center[0], center[1], d, d);
				} else {
					//TODO: support other shapes too
				}
			}
		}
	}

	/**
	 * Do a parallel projection of the given X,Y,Z coordinate. Ignore Axis.A and Axis.B.
	 */
	protected int[] projectPoint3D(final Vector3D vector3d, final double scale) {
		//TODO: avoid object creation
		int[] retval = new int[] {
			(int) ((mAxisHorizontal.get(vector3d) - this.mHorizontalMin) * scale),
			(int) ((mAxisVertical.get(vector3d) - this.mVerticalMin) * scale)
		};
		return retval;
	}
	/**
	 * Do a parallel projection of the given X,Y,Z,A,B coordinate.
	 * @param zOffset an offset to add onto the Z coordinate before projecting
	 */
	protected int[] projectPoint5D(final double[] coordinate, final double scale, final double zOffset) {
		//TODO: avoid object creation
		double[] c = new double[] {coordinate[0], coordinate[1], coordinate[2]};
		c[Axis.Z.ordinal()] += zOffset;

		// rotate around A
		Axis[] plane = Axis.A.getRotationPlane();
		double[] temp = Trigonometry.rotate2D(c[plane[0].ordinal()], c[plane[1].ordinal()], coordinate[Axis.A.ordinal()]);
		c[plane[0].ordinal()] = temp[0];
		c[plane[1].ordinal()] = temp[1];

		// rotate around B
		plane = Axis.B.getRotationPlane();
		temp = Trigonometry.rotate2D(c[plane[0].ordinal()], c[plane[1].ordinal()], coordinate[Axis.B.ordinal()]);
		c[plane[0].ordinal()] = temp[0];
		c[plane[1].ordinal()] = temp[1];
	
		int[] retval = new int[] {
			(int) ((c[mAxisHorizontal.ordinal()] - this.mHorizontalMin) * scale),
			(int) ((c[mAxisVertical.ordinal()] - this.mVerticalMin) * scale)
		};
		return retval;
	}
}
