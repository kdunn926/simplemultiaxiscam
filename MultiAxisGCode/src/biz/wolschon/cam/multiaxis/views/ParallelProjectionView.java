package biz.wolschon.cam.multiaxis.views;


import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.model.Triangle;
import biz.wolschon.cam.multiaxis.tools.BallShape;
import biz.wolschon.cam.multiaxis.tools.CylinderShape;
import biz.wolschon.cam.multiaxis.tools.IToolShape;
import biz.wolschon.cam.multiaxis.tools.Tool;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;
import biz.wolschon.cam.multiaxis.trigonometry.Trigonometry;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Panel that shows a parallel projection of a model and a tool.
 */
public class ParallelProjectionView extends JPanel implements ListDataListener, MouseListener, MouseMotionListener {
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
	private GCodeModel mGCodeModel;
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
	private int horizontalOffset;
	private int verticalOffset;
	private String mLabel;

	/**
	 * Show the geometry.
	 */
	private boolean mShowModel = true;
	/**
	 * Show the tool.
	 */
	private boolean mShowTool = true;
	/**
	 * Show the tool path.
	 */
	private boolean mShowPath = false;
	private BufferedImage mPathDoubleBuffer;
	private Limit mSegment;
	private ISegmentSelectionListener mSegmentSelectionListener;
	/**
	 * True if the user is currently selecting a segment.
	 */
	private boolean mSegmentSelecting = false;
	private int mSegmentEndX;
	private int mSegmentEndY;
	private int mSegmentStartX;
	private int mSegmentStartY;
	/**
	 * Used for double buffering.
	 */
	private static final GraphicsConfiguration mGraphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * @param aTool may be null
	 * @param aLabel 
	 */
	public ParallelProjectionView(final IModel aModel, final Tool aTool, final String aLabel) {
		this.mModel = aModel;
		this.mTool  = aTool;
		this.mLabel = aLabel;
		setVerticalAxis(Axis.Z);
		setHorizontalAxis(Axis.X);
		setMinimumSize(new Dimension(100, 100));
		setPreferredSize(new Dimension(500, 500));
		addMouseListener(this);
		addMouseMotionListener(this);
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
		updateUI();	
	}

	public void setHorizontalAxis(final Axis a) {
		if (this.mAxisHorizontal == a)  {
			return;
		}
		this.mAxisHorizontal = a;
		this.mHorizontalMin = mModel.getMin(this.mAxisHorizontal);
		this.mHorizontalMax = mModel.getMax(this.mAxisHorizontal);		
		this.mDoubleBuffer = null;
		updateUI();
	}

	/**
	 * @return the gCodeModel
	 */
	public GCodeModel getGCodeModel() {
		return mGCodeModel;
	}

	/**
	 * @param aGCodeModel the gCodeModel to set
	 */
	public void setGCodeModel(final GCodeModel aGCodeModel) {
		if (mGCodeModel != null) {
			mGCodeModel.removeListDataListener(this);
		}
		mGCodeModel = aGCodeModel;
		mGCodeModel.addListDataListener(this);
	}
	public boolean isShowModel() {
		return mShowModel;
	}

	public void setShowModel(boolean aShowModel) {
		mShowModel = aShowModel;
		repaint();
	}

	public boolean isShowTool() {
		return mShowTool;
	}

	public void setShowTool(boolean aShowTool) {
		mShowTool = aShowTool;
		repaint();
	}

	public boolean isShowPath() {
		return mShowPath;
	}

	public void setShowPath(boolean aShowPath) {
		mShowPath = aShowPath;
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
			paintGeometryToBuffer((Graphics2D) mDoubleBuffer.getGraphics());
		}
		if (isShowModel()) {
			g.drawImage(mDoubleBuffer, 0, 0, this);
		} else {
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		// draw tool path
		if (isShowPath() && getGCodeModel() != null) {
			if (mPathDoubleBuffer == null ||
				mPathDoubleBuffer.getWidth() != getWidth() ||
				mPathDoubleBuffer.getHeight() != getHeight()) {
				mPathDoubleBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);//mGraphicsConf.createCompatibleImage(getWidth(), getHeight());
				Graphics2D g2 = (Graphics2D) mPathDoubleBuffer.createGraphics();
				paintPathToBuffer(g2);
				g2.dispose();
			}
			g.drawImage(mPathDoubleBuffer, 0, 0, this);
			
		}
		// draw tool
		if (mTool != null && mToolLocation != null && isShowTool()) {
			g.setColor(Color.RED);
			for (IToolShape shape : mTool.getShape()) {
				// draw tool on top of wireframe
				if (shape instanceof BallShape) {
					BallShape ball = (BallShape) shape;
					int[] center = projectPoint5D(mToolLocation, ball.getLocation()+ball.getRadius(), true);
					//TODO: allow solid in addition to wireframe
					int d = (int) ( ball.getDiameter() * scale);
					g.drawOval(center[0] - d/2, center[1] - d/2, d, d);
				} else if (shape instanceof CylinderShape) {
					CylinderShape cyl = (CylinderShape) shape;
					int[] low = projectPoint5D(mToolLocation, cyl.getLocation(), true);
					int[] high = projectPoint5D(mToolLocation, cyl.getLocation() + cyl.getLength(), true);
					//TODO: allow solid in addition to wireframe
					g.drawLine(low[0], low[1], high[0], high[1]);
				} else {
					//TODO: support other shapes too
				}
			}
		}

		// draw current segment
		if (this.mSegment != null && this.mSegment.isAxisLimited(mAxisHorizontal) && this.mSegment.isAxisLimited(mAxisVertical)) {
			//TODO
			//TODO rotational axis too
		}

		// draw segment selection is progress
		if (mSegmentSelecting) {
			g.setColor(Color.BLACK);
			g.drawRect(mSegmentStartX, mSegmentStartY, mSegmentEndX - mSegmentStartX, mSegmentEndY - mSegmentStartY);
		}
	}

	/**
	 * Paint the tool path to the given graphics. Don't fill the background to allow for transparency.
	 * @param g the graphics of a BufferedImage
	 */
	private void paintPathToBuffer(final Graphics2D g) {
		// show tool path
		GCodeModel model = getGCodeModel();
		double[] lastLocation = null;
		int alpha = 80;
		g.setColor(new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getBlue(), Color.LIGHT_GRAY.getGreen(), alpha));
		for (int i = 0; i < model.getSize(); i++) {
			double[] location = model.getToolLocation(i);
			if (lastLocation != null) {
				int[] start = projectPoint5D(lastLocation, 0, true);
				int[] end = projectPoint5D(location, 0, true);
				g.drawLine(start[0], start[1], end[0], end[1]);
			}
			lastLocation = location;
		}
	}
	/**
	 * Do the actual painting of the geometry. Called by {@link #paintComponent(Graphics)}.
	 * @param g the graphics of a BufferedImage
	 */
	private void paintGeometryToBuffer(final Graphics2D g) {
		// fill background
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// prepare drawing the mesh
		double horizontalScale = getWidth() / (mHorizontalMax - mHorizontalMin);
		double verticalScale = getHeight() / (mVerticalMax - mVerticalMin);
		this.scale = Math.min(horizontalScale, verticalScale) / 3.0d;
		this.horizontalOffset = getWidth()/2;
		this.verticalOffset = getHeight()/2;

		// draw the mesh
		int count = mModel.getTriangleCount();
		g.setColor(Color.WHITE);
		for(int i=0; i<count; i++) {
			// project the 3 vertices of the triangle onto the screen
			// the model always has A=0 and B=0 coordinates
			Triangle triangle = mModel.getTriangle(i);
			int[] project0 = projectPoint3D(triangle.getP1());
			int[] project1 = projectPoint3D(triangle.getP2());
			int[] project2 = projectPoint3D(triangle.getP3());

			//TODO: allow solid in addition to wireframe
			g.drawLine(project0[0], project0[1], project1[0], project1[1]);
			g.drawLine(project1[0], project1[1], project2[0], project2[1]);
			g.drawLine(project2[0], project2[1], project0[0], project0[1]);
		}

		drawAxisNames(g);
	}

	private void drawAxisNames(final Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawChars(mAxisVertical.toString().toCharArray(), 0, 1, 10, 10);
		g.drawLine(5,  5,  5,  20);
		g.drawLine(5,  5,  7,  7);
		g.drawLine(5,  5,  3,  7);

		g.drawChars(mAxisHorizontal.toString().toCharArray(), 0, 1, 20, 17);
		g.drawLine(5,  20,  25,  20);
		g.drawLine(25,  20,  23,  23);
		g.drawLine(25,  20,  23,  18);

		if (mLabel != null && mLabel.length() > 0) {
			g.drawChars(mLabel.toCharArray(), 0, mLabel.length(), 40, 15);
		}
		
		g.setColor(Color.RED);
		Vector3D leftA = new Vector3D(mModel.getMinX(), mModel.getCenterY(), mModel.getCenterZ());
		Vector3D rightA = new Vector3D(mModel.getMaxX(), mModel.getCenterY(), mModel.getCenterZ());
		int[] project0 = projectPoint3D(leftA);
		int[] project1 = projectPoint3D(rightA);
		g.drawLine(project0[0],  project0[1],  project1[0],  project1[1]);
		g.drawChars(Axis.A.toString().toCharArray(), 0, 1, project0[0], project0[1] - 10);
		
	}

	/**
	 * Do a parallel projection of the given X,Y,Z coordinate. Ignore Axis.A and Axis.B.
	 */
	protected int[] projectPoint3D(final Vector3D aVector3d) {
		//TODO: avoid object creation
		int[] retval = new int[] {
			(int) ((mAxisHorizontal.get(aVector3d) - this.mHorizontalMin) * scale + horizontalOffset),
			(int) ((mAxisVertical.get(aVector3d) - this.mVerticalMin) * scale + verticalOffset)
		};
		return retval;
	}
	/**
	 * Do a parallel projection of the given X,Y,Z,A,B coordinate.
	 * @param aZOffset an offset to add onto the Z coordinate before projecting
	 * @param tool if true, the inverse of the rotation is applied since we are actually turning the object but show it as rotating the tool inverse
	 */
	protected int[] projectPoint5D(final double[] aCoordinate, final double aZOffset, boolean tool) {

		System.out.println("DEBUG: projectPoint5D " + Arrays.toString(aCoordinate));
		//TODO: avoid object creation
		double[] c = new double[] {aCoordinate[0], aCoordinate[1], aCoordinate[2]};
		c[Axis.Z.ordinal()] += aZOffset;

		// rotate around A
		Axis[] plane = Axis.A.getRotationPlane();
		double angle = aCoordinate[Axis.A.ordinal()];
		if (tool) {
			angle = -1.0d * angle;
		}
		double[] temp = Trigonometry.rotate2D(c[plane[0].ordinal()], c[plane[1].ordinal()], angle);
		c[plane[0].ordinal()] = temp[0];
		c[plane[1].ordinal()] = temp[1];

		// rotate around B
		if (aCoordinate.length > Axis.B.ordinal()) {
			plane = Axis.B.getRotationPlane();
			angle = aCoordinate[Axis.B.ordinal()];
			if (tool) {
				angle = -1.0d * angle;
			}
			temp = Trigonometry.rotate2D(c[plane[0].ordinal()], c[plane[1].ordinal()], angle);
			c[plane[0].ordinal()] = temp[0];
			c[plane[1].ordinal()] = temp[1];
		}
	
		int[] retval = new int[] {
			(int) ((c[mAxisHorizontal.ordinal()] - this.mHorizontalMin) * scale + horizontalOffset),
			(int) ((c[mAxisVertical.ordinal()] - this.mVerticalMin) * scale + verticalOffset)
		};
		return retval;
	}

	@Override
	public void contentsChanged(final ListDataEvent aGCodeListEvent) {
		mPathDoubleBuffer = null;
		repaint();
	}

	@Override
	public void intervalAdded(final ListDataEvent aGCodeListEvent) {
		mPathDoubleBuffer = null;
		repaint();
	}

	@Override
	public void intervalRemoved(final ListDataEvent aGCodeListEvent) {
		mPathDoubleBuffer = null;
		repaint();
	}

	/**
	 * There is at most one listener at any time.
	 * @param aStrategyCreationPanel
	 */
	public void setSegmentSelectionListener(final ISegmentSelectionListener aSegmentSelectionListener) {
		this.mSegmentSelectionListener = aSegmentSelectionListener;
	}

	public void setSegment(final Limit aSegment) {
		this.mSegment = aSegment;
	}

	public void setSegmentSelection(boolean aSelect) {
		this.mSegmentSelecting  = true;
	}

	@Override
	public void mouseDragged(final MouseEvent anEvent) {
		if (mSegmentSelecting) {
			this.mSegmentEndX = anEvent.getX();
			this.mSegmentEndY = anEvent.getY();
			repaint();
		}
	}

	@Override
	public void mouseMoved(final MouseEvent aArg0) {
		// ignored
	}

	@Override
	public void mouseClicked(final MouseEvent aArg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent aArg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent aArg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent anEvent) {
		if (mSegmentSelecting) {
			this.mSegmentStartX = anEvent.getX();
			this.mSegmentStartY = anEvent.getY();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent aArg0) {
		if (mSegmentSelecting) {
			this.mSegment = new Limit();

			double minFirstAxis = ((this.mSegmentStartX - horizontalOffset) / scale)  + this.mHorizontalMin;
			double maxFirstAxis = ((this.mSegmentEndX - horizontalOffset) / scale)  + this.mHorizontalMin;
			double minSecondAxis = ((this.mSegmentStartY - verticalOffset) / scale)  + this.mVerticalMin;
			double maxSecondAxis = ((this.mSegmentEndY - minSecondAxis) / scale)  + this.mVerticalMin;
			
			this.mSegment.addAxis(minFirstAxis, maxFirstAxis, mAxisHorizontal);
			this.mSegment.addAxis(minSecondAxis, maxSecondAxis, mAxisVertical);
			if (mSegmentSelectionListener != null) {
				this.mSegmentSelectionListener.onSegmentSelected(mSegment);
			}
			this.mSegmentSelecting = false;
		}
	}
}
