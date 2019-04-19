package plugins;

import java.nio.MappedByteBuffer;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class PluginReader {

	public static String read(String path) throws IOException {
		RandomAccessFile random = new RandomAccessFile(path, "r");
		byte[] buff = new byte[4096];
		random.read(buff);

		String line = new String(buff);
		StringBuilder builder = new StringBuilder();

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
		
		random.close();
		
		return builder.toString();
	}
}
