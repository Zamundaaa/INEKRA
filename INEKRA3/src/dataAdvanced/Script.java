package dataAdvanced;

import java.util.ArrayList;

import gameStuff.Err;
import toolBox.Tools;

public class Script {

	public static final String trenner = ";", cct = ":", coordt = "#";
	public static final String set = "SET", fill = "FILL", setifempty = "SETIFEMPTY", fillifempty = "FILLIFEMPTY",
			setTranslation = "SETTRANSLATION";
	public static final int NONE = 0, SET = 1, FILL = 2, ENDFILLING = 3, SETIFEMPTY = 4, FILLIFEMPTY = 5,
			SETTRANSLATION = 6;

	public static Script firstScript() {
		return loadScript("1.script");
	}

	protected ArrayList<Integer> x, y, z, command;
	protected ArrayList<Short> ids;

	public Script(String content) {
		try {
			interpret(removeBreaks(content));
		} catch (Exception e) {
			Err.err.println("interpreting a script with the content:" + System.lineSeparator() + content
					+ System.lineSeparator() + "went wrong!");
			e.printStackTrace(Err.err);
		}
	}

	private void interpret(String content) {
		x = new ArrayList<Integer>();
		y = new ArrayList<Integer>();
		z = new ArrayList<Integer>();
		command = new ArrayList<Integer>();
		ids = new ArrayList<Short>();
		String[] sections = content.split(trenner);
		String[] dunno;
		int c;
		for (int i = 0; i < sections.length; i++) {
			dunno = sections[i].split(cct);
			c = getCommandID(dunno[0]);
			if (c == FILL || c == FILLIFEMPTY) {
				dunno = dunno[1].split(coordt);
				x.add(Integer.parseInt(dunno[0]));
				y.add(Integer.parseInt(dunno[1]));
				z.add(Integer.parseInt(dunno[2]));
				x.add(Integer.parseInt(dunno[3]));
				y.add(Integer.parseInt(dunno[4]));
				z.add(Integer.parseInt(dunno[5]));
				command.add(c);
				command.add(ENDFILLING);
				short id = Short.parseShort(dunno[6]);
				ids.add(id);
				ids.add(id);
			} else if (c == SETTRANSLATION) {
				x.add(Integer.parseInt(dunno[0]));
				y.add(Integer.parseInt(dunno[1]));
				z.add(Integer.parseInt(dunno[2]));
				command.add(c);
				ids.add((short) 0);
			} else {
				dunno = dunno[1].split(coordt);
				x.add(Integer.parseInt(dunno[0]));
				y.add(Integer.parseInt(dunno[1]));
				z.add(Integer.parseInt(dunno[2]));
				command.add(c);
				ids.add(Short.parseShort(dunno[3]));
			}
		}
	}

	private int getCommandID(String cmd) {
		if (cmd.equalsIgnoreCase(set)) {
			return SET;
		} else if (cmd.equalsIgnoreCase(fill)) {
			return FILL;
		} else if (cmd.equalsIgnoreCase(setifempty)) {
			return SETIFEMPTY;
		} else if (cmd.equalsIgnoreCase(fillifempty)) {
			return FILLIFEMPTY;
		} else if (cmd.equalsIgnoreCase(setTranslation)) {
			return SETTRANSLATION;
		}
		// else if(cmd.equalsIgnoreCase()){
		return NONE;
	}

	/**
	 * @param filePosScriptFolder
	 *            the file name (/path) of the script inside the 'Scripts'
	 *            folder in 'INEKRA'
	 * @return
	 */
	public static Script loadScript(String filePosScriptsFolder) {
		String content = Tools.readFile("/Scripts/" + filePosScriptsFolder);
		return new Script(content);
	}

	public int getNOC() {
		return command.size();
	}

	public void sysout() {
		for (int i = 0; i < command.size(); i++) {
			System.out.println(getString(command.get(i)) + " X: " + x.get(i) + " Y: " + y.get(i) + " Z: " + z.get(i)
					+ " BID: " + ids.get(i));
		}
	}

	public String getString(int command) {
		switch (command) {
		case SET:
			return set;
		case FILL:
			return fill;
		case ENDFILLING:
			return "EFILL";
		case SETIFEMPTY:
			return setifempty;
		case FILLIFEMPTY:
			return fillifempty;
		default:
			return "UNDEFINED!";
		}
	}

	private String removeBreaks(String in) {
		String[] dunno = in.split(" ");
		String in2 = "";
		for (int i = 0; i < dunno.length; i++) {
			in2 += dunno[i];
		}
		dunno = in2.split("\n");
		in = "";
		for (int i = 0; i < dunno.length; i++) {
			in += dunno[i];
		}
		return in;
	}

}
