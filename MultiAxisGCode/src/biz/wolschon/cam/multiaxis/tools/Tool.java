package biz.wolschon.cam.multiaxis.tools;


/**
 * Possible axis of movement
 */
public class Tool {
	/**
	 * Construct a ball not cutter.
	 */
	public Tool(final double ballNoseDiameter) {
		this.shapes = new IToolShape[]{new BallShape(0, ballNoseDiameter)};
	}
	/**
	 * Construct an engraving tip cutter.
	 */
	public Tool(final double coneDiameter, final double coneLength) {
		this.shapes = new IToolShape[]{new ConeShape(0, coneDiameter, coneLength)};
	}
	/**
	 * Shapes this tool is made out of.
	 * Starting with the tip at index 0;
	 */
	private IToolShape[] shapes;
	public IToolShape getTipShape() {
		return shapes[0];
	}
	public IToolShape[] getShape() {
		return shapes;
	}
}
