package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;

/**
 * Strategy to call 2 child strategies.<br/>
 * WARNING! #endStrategy() is only called for our second child strategy!
 * @author marcuswolschon
 *
 */
public class ChainStrategy implements IStrategy, IProgressListener {
	/**
	 * The first of the two next strategies to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 */
	private IStrategy mNextStrategy0;
	/**
	 * The second of the two next strategies to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 */
	private IStrategy mNextStrategy1;
	private IProgressListener mProgressListener;

	/**
	 * @param aAxis the axis we move along in this strategy. Any value set by a previous strategy is overwritten.
	 * @param aStep The increment (in the current unit, e.g. "mm") we use to move along aAxis.
	 * @param aChild The next strategy to call
	 */
	public ChainStrategy(final IStrategy aChild0, final IStrategy aChild1) {
		this.mNextStrategy0 = aChild0;
		this.mNextStrategy1 = aChild1;
	}

	/**
	 * The first of the two next strategies to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 * @return the next strategy to call.
	 */
	public IStrategy getNextStrategy0() {
		return mNextStrategy0;
	}
	/**
	 * The second of the two next strategies to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 * @return the next strategy to call.
	 */
	public IStrategy getNextStrategy1() {
		return mNextStrategy1;
	}

	@Override
	public void runStrategy(final double aStartLocation[]) throws IOException {
		double aStartLocation2[] = Arrays.copyOf(aStartLocation, aStartLocation.length);
		getNextStrategy0().runStrategy(aStartLocation);
		getNextStrategy1().runStrategy(aStartLocation2);
	}

	/**
	 * #endStrategy() is only called for our second child strategy!
	 */
	@Override
	public void endStrategy()  throws IOException {
		getNextStrategy1().endStrategy();
	}

	public void addProgressListener(final IProgressListener aListener) {
		this.mProgressListener = aListener;
		getNextStrategy0().addProgressListener(this);
		getNextStrategy1().addProgressListener(this);
	}

	@Override
	public void onProgressChanged(final IStrategy aSender, final long aProgress, final long aMaximum) {
	
		if (aSender == getNextStrategy0()) {
			this.mProgressListener.onProgressChanged(this, aProgress, aMaximum * 2);
		}
		this.mProgressListener.onProgressChanged(this, aMaximum + aProgress, aMaximum * 2);
	}
}
