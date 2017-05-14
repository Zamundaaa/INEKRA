package gameStuff;

import java.io.*;

import toolBox.Tools;

public class Err extends PrintStream {

	public static Err err;
	public static FileOutputStream fout;

	static {
		try {
			File f = new File(Tools.getFolderPath() + "/latestlog.txt");
			if (!f.exists()) {
				f.createNewFile();
			}
			fout = new FileOutputStream(f);
			err = new Err(fout);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	private Err(OutputStream out) {
		super(out);
	}

	/**
	 * @comment calls err.println() and System.err.println()
	 */
	@Override
	public void println() {
		System.err.println();
		super.println();
	}

	/**
	 * @param x
	 * @comment calls err.println(x) and System.err.println(x)
	 */
	@Override
	public void println(String x) {
		System.err.println(x);
		super.print(x);
		super.println();
	}

	/**
	 * @param x
	 * @comment calls err.print(x) and System.err.print(x)
	 */
	@Override
	public void print(String x) {
		System.err.print(x);
		super.print(x);
	}

}
