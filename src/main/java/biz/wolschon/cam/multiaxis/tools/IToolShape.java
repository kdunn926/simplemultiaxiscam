package biz.wolschon.cam.multiaxis.tools;


/**
 * Genetic interface for shapes that make up the profile of a cutter tool.
 */
public interface IToolShape {

	/**
	 * @return Height of the lowest part of this shape above 0=tip.
	 */
	public double getLocation();
}
