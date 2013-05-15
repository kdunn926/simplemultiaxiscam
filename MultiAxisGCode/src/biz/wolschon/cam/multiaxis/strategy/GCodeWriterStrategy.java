package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.io.Writer;

public class GCodeWriterStrategy implements IStrategy {

	private Writer out;
	
	public GCodeWriterStrategy(final Writer out) {
		this.out = out;
	}
	@Override
	public void runStrategy(final double[] startLocation) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("G1 X").append(Double.toString(startLocation[0]));
		sb.append(" Y").append(Double.toString(startLocation[1]));
		sb.append(" Z").append(Double.toString(startLocation[2]));
		sb.append(" A").append(Double.toString(startLocation[3]));
		if (startLocation.length > 4) {
			sb.append(" B").append(Double.toString(startLocation[4]));
		}
		sb.append("\n");
		writeCodeLine(sb.toString(), startLocation);
		
		System.out.print(sb.toString()); //TODO: debug
		
	}
	
	protected void writeCodeLine(final String s, final double[] location) throws IOException {
		out.write(s);
	}

}
