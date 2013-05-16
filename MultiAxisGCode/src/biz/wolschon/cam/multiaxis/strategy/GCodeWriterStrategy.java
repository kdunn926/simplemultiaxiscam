package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.io.Writer;

public class GCodeWriterStrategy implements IStrategy {

	/*
	 * Where we write our G-Code to.
	 */
	private Writer mOut;
	
	/**
	 * @param aOutput where we write our G-Code to.
	 */
	public GCodeWriterStrategy(final Writer aOutput) {
		this.mOut = aOutput;
		//TODO: write a G-Code header (use metric coordinates, spin up the spindle)
	}

	@Override
	public void runStrategy(final double[] aNextToolLocation) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("G1 X").append(Double.toString(aNextToolLocation[0]));
		sb.append(" Y").append(Double.toString(aNextToolLocation[1]));
		sb.append(" Z").append(Double.toString(aNextToolLocation[2]));
		sb.append(" A").append(Double.toString(aNextToolLocation[3]));
		if (aNextToolLocation.length > 4) {
			sb.append(" B").append(Double.toString(aNextToolLocation[4]));
		}
		sb.append("\n");
		writeCodeLine(sb.toString(), aNextToolLocation);
		
		System.out.print(sb.toString()); //TODO: debug output
		
	}
	
	protected void writeCodeLine(final String s, final double[] location) throws IOException {
		mOut.write(s);
	}


	@Override
	public void endStrategy()  throws IOException {
		//TODO: write G-Code footer (spin down spindle, stop program)
	}
}
