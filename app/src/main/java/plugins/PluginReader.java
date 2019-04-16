package plugins;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;


public class PluginReader {
	
	public static String read(String path) throws IOException {

		FileInputStream file = new FileInputStream(path);

		BufferedInputStream bis = new BufferedInputStream(file);

		byte[] buffer = new byte[8192];

		bis.read(buffer, 0, buffer.length);

		String line = new String(buffer);

		StringBuilder builder = new StringBuilder();

		if (!line.contains("TES3")) // make sure this actually is a real TES3 data file
			return "";

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
				}
			}

		if (path.contains("Bloodmoon.esm")) {
			builder.append("Tribunal.esm \n");
		}
		
		bis.close();
		file.close();
		return builder.toString();
	}
}
