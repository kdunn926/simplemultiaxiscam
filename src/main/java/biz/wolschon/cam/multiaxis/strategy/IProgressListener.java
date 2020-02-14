/**
 * 
 */
package biz.wolschon.cam.multiaxis.strategy;

/**
 * @author marcuswolschon
 *
 */
public interface IProgressListener {

	public void onProgressChanged(final IStrategy aSender, final long aProgress, final long aMaximum);
}
