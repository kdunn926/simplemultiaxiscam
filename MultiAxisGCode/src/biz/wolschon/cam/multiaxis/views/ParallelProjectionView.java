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
import java.io.Serializable;
import java.util.Arrays;

import javax.swing.JPanel;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Panel that shows a parallel projection of a model and a tool.
 */
public class ParallelProjectionView extends JPanel {
	/**
	 * For {@link Serializable}.
	 */
	private static final long serialVersionUID = 6952384291024398143L;
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
	private double scale;
	/**
	 * Used for double buffering.
	 */
	private static final GraphicsConfiguration mGraphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * @param aTool may be null
	 */
	public ParallelProjectionView(final IModel aModel, final Tool aTool) {
		this.mModel = aModel;
		this.mTool  = aTool;
		setVerticalAxis(Axis.Z);
		setHorizontalAxis(Axis.X);
	}

	public void setToolLocation(final double[] aToolLocation) {
		this.mToolLocation = aToolLocation;
		if (this.mTool != null)  {
			//this.mDoubleBuffer = null;
			repaint();
		}
	}
	public void setTool(final Tool aTool) {

		this.mTool = aTool;
		if (this.mToolLocation != null)  {
			//this.mDoubleBuffer = null;
			repaint();
		}
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
	public void paintComponent(Graphics g) {
		if (mDoubleBuffer == null ||
		    mDoubleBuffer.getWidth() != getWidth() ||
		    mDoubleBuffer.getHeight() != getHeight()) {
			mDoubleBuffer = mGraphicsConf.createCompatibleImage(getWidth(), getHeight());
			paintToBuffer((Graphics2D) mDoubleBuffer.getGraphics());
		}
		g.drawImage(mDoubleBuffer, 0, 0, this);
		
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
	 * Do the actual painting. Called by #paintcomponent(Graphics);
	 */
	private void paintToBuffer(final Graphics2D g) {
		// fill background
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw mesh
		double horizontalScale = getWidth() / (mHorizontalMax - mHorizontalMin);
		double verticalScale = getHeight() / (mVerticalMax - mVerticalMin);
		this.scale = Math.min(horizontalScale, verticalScale);
		//TODO: draw mesh
		int count = mModel.getTriangleCount();
		g.setColor(Color.WHITE);
		for(int i=0; i<count; i++) {
			// project the 3 vertices of the triangle onto the screen
			// the model always has A=0 and B=0 coordinates
			Triangle triangle = mModel.getTriangle(i);
			int[] project0 = projectPoint3D(triangle.getP1(), scale);
			int[] project1 = projectPoint3D(triangle.getP2(), scale);
			int[] project2 = projectPoint3D(triangle.getP3(), scale);
/*System.out.println("painting triangle #" + i + " at "
		+ project0[0] + ":" + project0[1] + " - "
		+ project1[0] + ":" + project1[1] + " - "
		+ project2[0] + ":" + project2[1] + " - ");*/
			//TODO: allow solid in addition to wireframe
			g.drawLine(project0[0], project0[1], project1[0], project1[1]);
			g.drawLine(project1[0], project1[1], project2[0], project2[1]);
			g.drawLine(project2[0], project2[1], project0[0], project0[1]);
		}

		
	}

	/**
	 * Do a parallel projection of the given X,Y,Z coordinate. Ignore Axis.A and Axis.B.
	 */
	protected int[] projectPoint3D(final Vector3D aVector3d, final double aScale) {
		//TODO: avoid object creation
		int[] retval = new int[] {
			(int) ((mAxisHorizontal.get(aVector3d) - this.mHorizontalMin) * aScale),
			(int) ((mAxisVertical.get(aVector3d) - this.mVerticalMin) * aScale)
		};
		return retval;
	}
	/*
	 * Do a parallel projection of the given X,Y,Z,A,B coordinate.
	 * @param aZOffset an offset to add onto the Z coordinate before projecting
	 */
	protected int[] projectPoint5D(final double[] aCoordinate, final double aScale, final double aZOffset) {

		System.out.println("DEBUG: projectPoint5D " + Arrays.toString(aCoordinate));
		//TODO: avoid object creation
		double[] c = new double[] {aCoordinate[0], aCoordinate[1], aCoordinate[2]};
		c[Axis.Z.ordinal()] += aZOffset;

		// rotate around A
		Axis[] plane = Axis.A.getRotationPlane();
		double[] temp = Trigonometry.rotate2D(c[plane[0].ordinal()], c[plane[1].ordinal()], aCoordinate[Axis.A.ordinal()]);
		c[plane[0].ordinal()] = temp[0];
		c[plane[1].ordinal()] = temp[1];

		// rotate around B
		if (aCoordinate.length > Axis.B.ordinal()) {
			plane = Axis.B.getRotationPlane();
			temp = Trigonometry.rotate2D(c[plane[0].ordinal()], c[plane[1].ordinal()], aCoordinate[Axis.B.ordinal()]);
			c[plane[0].ordinal()] = temp[0];
			c[plane[1].ordinal()] = temp[1];
		}
	
		int[] retval = new int[] {
			(int) ((c[mAxisHorizontal.ordinal()] - this.mHorizontalMin) * aScale),
			(int) ((c[mAxisVertical.ordinal()] - this.mVerticalMin) * aScale)
		};
		return retval;
	}
}
