package biz.wolschon.cam.multiaxis.tools;


/**
 * Possible axis of movement
 */
public class Tool {
	private String mToolName;

	//private Tool(IToolShape mShapes[]) {
	//	this.shapes = mShapes;
	//}
	private Tool(final String aToolName, IToolShape shapes0, IToolShape shapes1) {
		this.mShapes = new IToolShape[] {shapes0, shapes1};
		this.mToolName = aToolName;
	}
	/**
	 * Construct a ball not cutter.
	 */
	protected Tool(final String aToolName, final double aBallNoseDiameter) {
		this.mShapes = new IToolShape[]{
				new BallShape(0, aBallNoseDiameter),
				new CylinderShape(aBallNoseDiameter/2.0d, aBallNoseDiameter/2.0d, 50d),
				};
		this.mToolName = aToolName;
	}
	/**
	 * Construct a ball nose cutter.
	 */
	public static Tool createBallCutter(final String aToolName, final double aBallNoseDiameter, final double aToolLength, final double aShaftDiameter) {
		return new Tool(aToolName, new BallShape(0, aBallNoseDiameter),
				new CylinderShape(aBallNoseDiameter/2.0d, aShaftDiameter, aToolLength - aBallNoseDiameter/2.0d)
				);
	}
	/**
	 * Construct a flat cutter.
	 */
	public static Tool createFlatCutter(final String aToolName, final double aCutterDiameter, final double aCutterLength, final double aToolLength, final double aShaftDiameter) {
		return new Tool(aToolName, new CylinderShape(0, aCutterDiameter, aCutterLength),
				new CylinderShape(aCutterLength, aShaftDiameter, aToolLength - aCutterLength)
				);
	}
	/**
	 * Construct an engraving tip cutter.
	 * @param aToolName 
	 */
	protected Tool(final String aToolName, final double aConeDiameter, final double aConeLength) {
		this.mShapes = new IToolShape[]{new ConeShape(0, aConeDiameter, aConeLength)};
		this.mToolName = aToolName;
	}
	/**
	 * Shapes this tool is made out of.
	 * Starting with the tip at index 0;
	 */
	private IToolShape[] mShapes;

	/**
	 * @return the shape that sits at the tip of the cutter.
	 */
	public IToolShape getTipShape() {
		return mShapes[0];
	}
	/**
	 * @return all mShapes that make up the cutter.
	 */
	public IToolShape[] getShape() {
		return mShapes;
	}

	@Override
	public String toString() {
		return mToolName;
	}
}
