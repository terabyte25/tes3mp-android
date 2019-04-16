package plugins;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PluginReader {
	
	public static String read(String path) throws IOException {

		int lineNum = 0;

		FileInputStream file = new FileInputStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(file));
		String line = reader.readLine();
		StringBuilder builder = new StringBuilder();

		if (!line.contains("TES3")) // make sure this actually is a real TES3 data file
			return "";

		while (line != null || lineNum < 5) {
			int count = line.split("MAST").length;
			if (count > 0)
				for (int i = 0; i < count; i++) {
					try {
						String[] data = line.split("MAST");
						if (data[i].contains(".esp")
								|| data[i].contains(".ESP")) {

							builder.append(data[i].split(".esp")[0].replaceAll(
									"\\p{Cntrl}", "") + ".esp");
							builder.append("\n");
						} else if (data[i].contains(".esm")
								|| data[i].contains(".ESM")) {
							builder.append(data[i].split(".esm")[0].replaceAll(
									"\\p{Cntrl}", "") + ".esm");
							builder.append("\n");
						}

						else if (data[i].contains(".omwaddon")) {
							builder.append(data[i].split(".omwaddon")[0]
									.replaceAll("\\p{Cntrl}", "") + ".omwaddon");
							builder.append("\n");
						} else if (data[i].contains(".omwgame")) {
							builder.append(data[i].split(".omwgame")[0]
									.replaceAll("\\p{Cntrl}", "") + ".omwgame");
							builder.append("\n");
						}

					} catch (Exception e) {
						reader.close();
						return builder.toString();
					}
				}
			if (line.contains("NAME"))
				break;
			line = reader.readLine();
			lineNum++;
		}

		if (path.contains("Bloodmoon.esm")) {
			builder.append("Tribunal.esm \n");
		}
		
		reader.close();
		return builder.toString();
	}
}