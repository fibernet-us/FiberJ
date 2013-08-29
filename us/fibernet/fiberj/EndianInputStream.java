package us.fibernet.fiberj;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EndianInputStream extends DataInputStream {
	private boolean bigendian;

	public EndianInputStream(String fileName, boolean be)
			throws FileNotFoundException {
		super(new FileInputStream(fileName));
		bigendian = be;

	}

	public EndianInputStream(InputStream is, boolean be) {
		super(is);
		bigendian = be;
	}

	public int readShortCorrect() throws IOException {
		int val;

		if (bigendian) {
			val = readUnsignedShort();
			return val;
		} else {
			int high = readUnsignedByte();
			int low = readUnsignedByte();
			val = (int) ((high << 8) | low);
			return val;
		}
	}

}