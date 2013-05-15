package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.io.Writer;

public class GCodeWriterStrategy implements IStrategy {

	private Writer out;
	
	public GCodeWriterStrategy(final Writer out) {
		this.out = out;
	}
	@Override
	public void runStrategy(double[] startLocation) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("G1 X").append(Double.toString(startLocation[0]));
		sb.append(" Y").append(Double.toString(startLocation[1]));
		sb.append(" Z").append(Double.toString(startLocation[2]));
		sb.append(" A").append(Double.toString(startLocation[3]));
		sb.append("\n");
		out.write(sb.toString());
		
		System.out.print(sb.toString()); //TODO: debug
		
	}

}
