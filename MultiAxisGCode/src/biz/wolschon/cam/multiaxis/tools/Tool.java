package biz.wolschon.cam.multiaxis.tools;


/**
 * Possible axis of movement
 */
public class Tool {
	//private Tool(IToolShape shapes[]) {
	//	this.shapes = shapes;
	//}
	private Tool(IToolShape shapes0, IToolShape shapes1) {
		this.shapes = new IToolShape[] {shapes0, shapes1};
	}
	/**
	 * Construct a ball not cutter.
	 */
	public Tool(final double aBallNoseDiameter) {
		this.shapes = new IToolShape[]{
				new BallShape(0, aBallNoseDiameter),
				new CylinderShape(aBallNoseDiameter/2.0d, aBallNoseDiameter/2.0d, 50d),
				};
	}
	/**
	 * Construct a ball nose cutter.
	 */
	public static Tool createBallCutter(final double aBallNoseDiameter, final double aToolLength, final double aShaftDiameter) {
		return new Tool(new BallShape(0, aBallNoseDiameter),
				new CylinderShape(aBallNoseDiameter/2.0d, aShaftDiameter, aToolLength - aBallNoseDiameter/2.0d)
				);
	}
	/**
	 * Construct a flat cutter.
	 */
	public static Tool createFlatCutter(final double aCutterDiameter, final double aCutterLength, final double aToolLength, final double aShaftDiameter) {
		return new Tool(new CylinderShape(0, aCutterDiameter, aCutterLength),
				new CylinderShape(aCutterLength, aShaftDiameter, aToolLength - aCutterLength)
				);
	}
	/**
	 * Construct an engraving tip cutter.
	 */
	public Tool(final double aConeDiameter, final double aConeLength) {
		this.shapes = new IToolShape[]{new ConeShape(0, aConeDiameter, aConeLength)};
	}
	/**
	 * Shapes this tool is made out of.
	 * Starting with the tip at index 0;
	 */
	private IToolShape[] shapes;

	/**
	 * @return the shape that sits at the tip of the cutter.
	 */
	public IToolShape getTipShape() {
		return shapes[0];
	}
	/**
	 * @return all shapes that make up the cutter.
	 */
	public IToolShape[] getShape() {
		return shapes;
	}
}
