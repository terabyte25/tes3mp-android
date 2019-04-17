package plugins;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;


public class PluginReader {
	
	public static String read(String path) throws IOException {

		FileInputStream file = new FileInputStream(path);

		BufferedInputStream bis = new BufferedInputStream(file);

		byte[] buffer = new byte[8192];

		bis.read(buffer, 0, buffer.length);

		String line = new String(buffer);

		bis.close();
		file.close();

		if (!line.contains("TES3")) // make sure this actually is a real TES3 data file
			return "";

		List<String> strings = Arrays.asList( line.replaceAll("^.*?MAST", "")
			.split("DATA.*?(MAST|$)"));

		String dependencies = "";

		for (String s : strings) {
			dependencies += (s + "\n");
		}

		if (path.contains("Bloodmoon.esm")) {
			dependencies += ("Tribunal.esm \n");
		}

		return dependencies;
	}
}
