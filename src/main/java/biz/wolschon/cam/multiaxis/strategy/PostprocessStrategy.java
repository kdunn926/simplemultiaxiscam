package biz.wolschon.cam.multiaxis.strategy;

import java.io.File;
import java.io.IOException;

/**
 * Strategy to load G-Code from a file for post-processing.
 * e.g. with the {@link FollowSurfaceNormalCutStrategy}.
 * Status: Work in progress
 * @author marcuswolschon
 *
 */
public class PostprocessStrategy implements IStrategy, IProgressListener {

	private File mSourceFile;
	private long mProgressMax;
	private int mProgress;
	private IProgressListener mProgressListener;
	/**
	 * @param aSourceFile source g-code file to postprocess
	 */
	public PostprocessStrategy(final File aSourceFile) {
		this.mSourceFile = aSourceFile;
	}


	@Override
	public void runStrategy(final double aStartLocation[], final boolean isCutting) throws IOException {
		//TODO
		
	}


	@Override
	public void endStrategy()  throws IOException {
		// we have nothing to finish/clean up but maybe the next strategy has.
		//TODO
	}

	public void addProgressListener(final IProgressListener aListener) {
		this.mProgressListener = aListener;
	}

	@Override
	public void onProgressChanged(IStrategy aSender, long aProgress,
			long aMaximum) {
		this.mProgressListener.onProgressChanged(this, mProgress * aMaximum + aProgress, aMaximum * mProgressMax);
		
	}
}
