/**
 * 
 */
package biz.wolschon.cam.multiaxis.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import biz.wolschon.cam.multiaxis.settings.Preferences;

/**
 * Stores all defined tools in {@link Preferences#getToolsDirectory()}.<br/>
 * The file format is compatible with DeskProto (but does not support everything DeskProto does).
 * @author marcuswolschon
 *
 */
public class ToolRepository {

	private static final String TOOLFILEPOSTFIX = ".ctr";

	/**
	 * All defined tools.
	 */
	private Collection<Tool> mTools = new LinkedList<Tool>();

	public ToolRepository(final Preferences prefs) {
		File dir = prefs.getToolsDirectory();
		File[] toolFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(final File aFile) {
				return aFile.getName().toLowerCase().endsWith(TOOLFILEPOSTFIX);
			}
		});
		if (toolFiles == null) {
			System.err.println("No tools in " + dir.getAbsolutePath());
		} else {
			for (File file : toolFiles) {
				loadTool(file);
			}
			System.out.println("loaded " + mTools.size() + " tools");
		}
	}

	/**
	 * Load a tool in a format compatible with DeskProto.
	 * @param aFile
	 */
	private void loadTool(final File aFile) {
		try {
			FileReader reader = new FileReader(aFile);
			BufferedReader br = new BufferedReader(reader);
			try {
				String line = null;
				String toolName = null;
				int toolType = -1;
				double toolDiameter = 0;
				double toolCuttingLength = 0;
				double toolFreeLength  = 0;
				double toolShaftDiameter = 0;
				while ((line = br.readLine()) != null) {
					int index = line.indexOf('=');
					if (index < 1) {
						continue;
					}
					String name = line.substring(0, index).trim();
					String value = line.substring(index + 1, line.length()).trim();
					if (name.equalsIgnoreCase("name")) {
						toolName = value;
					} else if (name.equalsIgnoreCase("version")) {
						// ignored
					} else if (name.equalsIgnoreCase("NumberInMachine")) {
						// ignored
					} else if (name.equalsIgnoreCase("AutoSpindlespeed")) {
						// ignored
					} else if (name.equalsIgnoreCase("AutoFeedrate")) {
						// ignored
					} else if (name.equalsIgnoreCase("UseAutoSpeeds")) {
						// ignored
					} else if (name.equalsIgnoreCase("type")) {
						toolType = Integer.parseInt(value);
					} else if (name.equalsIgnoreCase("TipDiameter")) {
						//toolTipDiameter = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("FreeLength")) {
						toolFreeLength = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("CuttingLength")) {
						toolCuttingLength = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("FluteLength")) {
						//toolFluteLength = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("Angle")) {
						//toolAngle = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("SlopeAngle")) {
						//toolSlopeAngle = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("MaximumSpindlespeed")) {
						//toolMaximumSpindlespeed = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("ShaftDiameter")) {
						toolShaftDiameter = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("MultipleDiameter")) {
						if (Boolean.parseBoolean(value)) {
							System.err.println("tool '" + toolName + "' with multiple diameters not supported");
							return;
						}
					} else if (name.equalsIgnoreCase("diameter")) {
						toolDiameter = Double.parseDouble(value);
					} else {
						System.out.println("unknown tool property of tool '" + toolName + "'");
						System.out.println("name=" + name);
						System.out.println("value=" + value);
						
					}
				}
				
				switch (toolType) {
				case 0: {
					Tool flatTip = Tool.createFlatCutter(toolName, toolDiameter, toolCuttingLength, toolFreeLength, toolShaftDiameter);
					addTool(flatTip);
					break;
				}
				//case 2 : conic engraving tools
				case 3: {
					Tool ballNose = Tool.createBallCutter(toolName, toolDiameter, toolFreeLength, toolShaftDiameter);
					addTool(ballNose);
					break;
				}
				//cae 4 : smattened tools
				default: System.err.println("unknown tool type " + toolType + " of tool '" + toolName + "'");
				}
				
			} finally {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param ballNose
	 */
	protected void addTool(Tool ballNose) {
		mTools.add(ballNose);
	}
}
