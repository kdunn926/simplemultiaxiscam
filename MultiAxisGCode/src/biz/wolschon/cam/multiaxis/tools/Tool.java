package biz.wolschon.cam.multiaxis.tools;


/**
 * Possible axis of movement
 */
public class Tool {
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
