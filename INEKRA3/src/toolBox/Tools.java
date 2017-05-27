package toolBox;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import gameStuff.Err;

/**
 * @author xaver
 *
 */
public class Tools {

	public static boolean mouseGrabbed = true;
	public static final String superFolder = "INEKRA";
	public static final String superpath;
	public static final String screenShotFolder;
	public static final String scriptFolderInINEKRA = "/Scripts/";
	public static final String scriptFolder;

	static {
		superpath = System.getProperty("user.home") + "/" + superFolder;
		// File folder = new File(superpath + "/");
		// if (!folder.exists()) {
		// folder.mkdirs();
		// }
		File folder = new File(superpath + scriptFolderInINEKRA);
		scriptFolder = superpath + scriptFolderInINEKRA;
		if (!folder.exists()) {
			folder.mkdirs();
		}
		screenShotFolder = superpath + "/Screenshots/";
		folder = new File(screenShotFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public static String[] getFiles(String folder) {
		String[] ret;
		File Folder;
		Folder = new File(superpath + "/" + folder);
		if (Folder.exists() && Folder.list() != null) {
			ret = Folder.list();
		} else {
			Folder.mkdirs();
			ret = Folder.list();
		}
		// File playing = new File(Folder.getPath() + "/" + musics[playin]);
		return ret;
	}

	public static URL getURLFile(String name) {
		return Tools.class.getClass().getResource(name);
	}

	public static InputStream getFile(String name) {
		return Tools.class.getClassLoader().getResourceAsStream(name);
	}

	private static void testPreferenceData() {
		String content = readFile("preferences.txt");
		if (content == null || content.equals("")) {
			String c2 = readJarFile("toolBox/preferences.txt");
			writeToFile("preferences.txt", c2);
		}
	}

	public static long loadLongPreference(String name, long standardValue) {
		testPreferenceData();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getFolderPath() + "/preferences.txt")));
			String line;
			line = reader.readLine();
			while (line != null) {
				if (line.split(" ")[0].equals(name)) {
					break;
				} else {
					line = reader.readLine();
				}
			}
			reader.close();
			if (line == null) {
				Err.err.println("Preference not found! Setting it default to " + standardValue);
				setLongPreference(name, standardValue);
				return standardValue;
			}
			String[] things = line.split(name + " = ");
			if (things.length != 2) {
				Err.err.println("Preference with wrong formation found! Setting it default to 0");
				setLongPreference(name, standardValue);
				return standardValue;
			}
			try {
				return Long.parseLong(things[1]);
			} catch (Exception e) {
				Err.err.println("some error occurred parsing '" + things[1] + "' to long. Returning the standard value "
						+ standardValue + " instead");
				return standardValue;
			}
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while loading a (float)Preference ocurred");
			System.exit(-1);
		}
		return standardValue;
	}

	public static void setLongPreference(String name, long value) {
		testPreferenceData();
		try {
			preferences = readFile("preferences.txt");
			String[] strs = preferences.split("\n");
			String newContent = strs[0];
			boolean gotit = false;
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].split(" ")[0].equals(name)) {
					newContent += name + " = " + value + "\n";
					gotit = true;
				} else {
					newContent += strs[i] + "\n";
				}
			}
			if (!gotit) {
				newContent += name + " = " + value + "\n";
				Err.err.println("didn't find preference '" + name + "'; writing it new");
			}
			writeToFile("", "preferences.txt", newContent, true);
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while writing a (long)Preference ocurred (Preference: " + name + ")");
			System.exit(-1);
		}
	}

	public static boolean loadBoolPreference(String name, boolean standardValue) {
		testPreferenceData();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getFolderPath() + "/preferences.txt")));
			String line = reader.readLine();
			while (line != null) {
				if (line.split(" ")[0].equals(name)) {
					break;
				} else {
					line = reader.readLine();
				}
			}
			reader.close();
			if (line == null) {
				setBoolPreference(name, standardValue);
				Err.err.println("Didn't find the preference " + name + "; Setting it to " + standardValue + " for now");
				return standardValue;
			}
			String[] things = line.split(name + " = ");
			if (things.length != 2) {
				throw new RuntimeException("Preferences with wrong formation found!");
			}
			if (things[1].equals("true")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while loading a (bool)Preference ocurred");
			System.exit(-1);
		}
		return false;
	}

	public static boolean loadBoolPreference(String name) {
		testPreferenceData();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getFolderPath() + "/preferences.txt")));
			String line = reader.readLine();
			while (line != null) {
				if (line.split(" ")[0].equals(name)) {
					break;
				} else {
					line = reader.readLine();
				}
			}
			reader.close();
			if (line == null) {
				setBoolPreference(name, false);
				Err.err.println("Tryed loading the " + name
						+ " preference. Didn't find it. Now it's false. Correct it if it's wrong!");
				return false;
			}
			String[] things = line.split(name + " = ");
			if (things.length != 2) {
				Err.err.println("Preferences with wrong formation found!");
				System.exit(-1);
			}
			if (things[1].equals("true")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while loading a (bool)Preference ocurred");
			System.exit(-1);
		}
		return false;
	}

	public static void setBoolPreference(String name, boolean bool) {
		testPreferenceData();
		try {
			String preferences = readFile("preferences.txt");
			String[] strs = preferences.split("\n");
			String newContent = "";
			boolean gotit = false;
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].split(" ")[0].equals(name)) {
					newContent = newContent + name + " = " + bool + "\n";
					gotit = true;
				} else {
					newContent = newContent + strs[i] + "\n";
				}
			}
			if (!gotit) {
				newContent += name + " = " + bool + "\n";
				Err.err.println("didn't find preference '" + name + "'; writing it new || OLD CONTENT: ");
			}
			writeToFile("", "preferences.txt", newContent, true);
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while writing a (bool)Preference ocurred");
			System.exit(-1);
		}
	}

	private static String preferences;

	public static void setFloatPreference(String name, float f) {
		testPreferenceData();
		try {
			preferences = readFile("preferences.txt");
			String[] strs = preferences.split("\n");
			String newContent = strs[0];
			boolean gotit = false;
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].split(" ")[0].equals(name)) {
					newContent += name + " = " + f + "\n";
					gotit = true;
				} else {
					newContent += strs[i] + "\n";
				}
			}
			if (!gotit) {
				newContent += name + " = " + f + "\n";
				Err.err.println("didn't find preference '" + name + "'; writing it new");
			}
			writeToFile("", "preferences.txt", newContent, true);
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while writing a (float)Preference ocurred");
			System.exit(-1);
		}
	}

	public static float loadFloatPreference(String name) {
		testPreferenceData();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getFolderPath() + "/preferences.txt")));
			String line;
			line = reader.readLine();
			while (line != null) {
				if (line.split(" ")[0].equals(name)) {
					break;
				} else {
					line = reader.readLine();
				}
			}
			reader.close();

			String[] things = line.split(name + " = ");
			if (things.length != 2) {
				Err.err.println("Preference with wrong formation found! Setting it default to 0");
				setFloatPreference(name, 0);
				return 0;
			}
			return Float.parseFloat(things[1]);

		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while loading a (bool)Preference ocurred");
			System.exit(-1);
		}
		return 0;
	}

	public static void writeToFile(String pathInsideThingsFolder, String context) {
		File f = new File(getFolderPath() + "/" + pathInsideThingsFolder);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(context);
			writer.close();
		} catch (Exception e) {
			Err.err.println("A error while writing to File " + f.getAbsolutePath() + " ocurred!");
			e.printStackTrace(Err.err);
			System.exit(-1);
		}
	}

	public static void writeToFile(String folderPathInsideApplicationFolder, String dataName, String context,
			boolean overwrite) {
		File f = new File(getFolderPath() + "/" + folderPathInsideApplicationFolder);
		try {
			if (!f.exists()) {
				f.mkdirs();
			}
			f = new File(f.getPath() + "/" + dataName);
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(f, !overwrite));
			writer.write(context);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("A error while writing to File " + f.getAbsolutePath() + " ocurred!");
			System.exit(-1);
		}
	}

	public static boolean fileThere(String pathInsideThingsFolder) {// ziemlich
																	// ineffizient;
																	// wird oft
																	// aufgerufen;
																	// -->
																	// static-Block
																	// zum
																	// erstellen
																	// des
																	// SuperFolders?
		File f = new File(superpath + "/" + pathInsideThingsFolder);
		return f.exists();
	}

	/**
	 * reads the file in the location specified by the pathInsideThingsFolder String
	 * @param pathInsideThingsFolder
	 * @return the content of the file if possible, otherwise null
	 */
	public static String readFile(String pathInsideThingsFolder) {
		File f = new File(superpath + "/" + pathInsideThingsFolder);
		if (!f.exists()) {
			return null;
		} else {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String text = "";
				String line = reader.readLine();
				while (line != null) {
					text += line + "\n";
					line = reader.readLine();
				}
				reader.close();
				return text;
			} catch (Exception e) {
				e.printStackTrace(Err.err);
				Err.err.println("Error while reading File " + f.getAbsolutePath());
				System.exit(-1);
			}
			return null;
		}
	}
	
	public static String readJarFile(String path) {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(Tools.class.getClassLoader().getResourceAsStream(path)));
		try {
			String text = "";
			String line = reader.readLine();
			while (line != null) {
				text += line + "\n";
				line = reader.readLine();
			}
			reader.close();
			return text;
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("Error while reading File " + path);
			System.exit(-1);
		}
		return null;
	}

	public static String getFolderPath() {
		return superpath;
	}

	public static void update() {

	}

	public static float loadFloatPreference(String name, float defaultValueIfPreferenceIsNotPresent) {
		testPreferenceData();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getFolderPath() + "/preferences.txt")));
			String line;
			line = reader.readLine();
			while (line != null) {
				if (line.startsWith(name)) {
					break;
				} else {
					line = reader.readLine();
				}
			}
			reader.close();
			if (line == null) {
				System.err
						.println("Preference not found! Setting it default to " + defaultValueIfPreferenceIsNotPresent);
				setFloatPreference(name, defaultValueIfPreferenceIsNotPresent);
				return defaultValueIfPreferenceIsNotPresent;
			}
			String[] things = line.split(name + " = ");
			if (things.length != 2) {
				Err.err.println("Preference with wrong formation found! Setting it default to 0");
				setFloatPreference(name, defaultValueIfPreferenceIsNotPresent);
				return defaultValueIfPreferenceIsNotPresent;
			}
			return Float.parseFloat(things[1]);
		} catch (Exception e) {
			e.printStackTrace(Err.err);
			Err.err.println("An error while loading a (float)Preference ocurred");
			System.exit(-1);
		}
		return defaultValueIfPreferenceIsNotPresent;
	}

	/**
	 * Write an array of bytes to a file
	 */
	public static void writeFileAsBytes(String fullPath, byte[] bytes) throws IOException {
		File f = new File(fullPath);
		if (!f.exists()) {
			f.createNewFile();
		}
		OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f));
		InputStream inputStream = new ByteArrayInputStream(bytes);
		int token = -1;

		while ((token = inputStream.read()) != -1) {
			bufferedOutputStream.write(token);
		}
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
		inputStream.close();
	}

	public static void writeBytes(String pathToFileInFolder, String fileName, byte[] bytes) {
		try {
			File ordner = new File(superpath + "/" + pathToFileInFolder);
			if (!ordner.exists()) {
				ordner.mkdirs();
				ordner.createNewFile();
			}
			writeFileAsBytes(superpath + "/" + pathToFileInFolder + fileName, bytes);
		} catch (IOException e) {
			e.printStackTrace(Err.err);
			Err.err.println("something went wrong while writing a bytes array to " + superpath + "/"
					+ pathToFileInFolder + fileName);
		}
	}

	public static byte[] readBytes(String fileInFolder) {
		File file = new File(superpath + "/" + fileInFolder);
		try {
			if (file.exists()) {
				return Files.readAllBytes(file.toPath());
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace(Err.err);
			System.exit(-1);
			return null;
		}
	}

	public static ByteBuffer readBytesRel(String relativePath) {
		try {
			InputStream I = Tools.class.getClassLoader().getResourceAsStream(relativePath);
			ArrayList<Byte> bytes = new ArrayList<Byte>();
			byte[] input = new byte[1];
			int in = I.read(input);
			while (in != -1) {
				bytes.add(input[0]);
				in = I.read(input);
			}
			I.close();
			ByteBuffer b = BufferUtils.createByteBuffer(bytes.size());
			for (int i = 0; i < bytes.size(); i++) {
				b.put(bytes.get(i));
			}
			b.flip();
			return b;
		} catch (IOException e) {
			Err.err.println("Failed to read " + relativePath + " to a ByteBuffer!");
			e.printStackTrace(Err.err);
			return null;
		}
	}

	public static void deleteDirectoryInINEKRA(String path) throws IOException {
		delete(new File(superpath + "/" + path + "/"));
	}
	
	private static void delete(File f) throws IOException{
		if(f.exists()){
			File[] contents = f.listFiles();
			if(contents != null){
				for(File f2: contents){
					delete(f2);
				}
			}
			Files.delete(f.toPath());
		}else{
			Err.err.println("File to delete does not exist!!! Absolute path: " + f.getAbsolutePath());
		}
	}

	public static OutputStream getOutputStreamFromFile(String fileInINEKRAfolder) throws IOException {
		File f = new File(superpath + "/" + fileInINEKRAfolder);
		if(!f.exists()){
			File parent = f.getParentFile();
			if(!parent.exists())
				parent.mkdirs();
			f.createNewFile();
		}
		return new FileOutputStream(f);
	}

	public static BufferedWriter getBufferedFileWriter(String fileInINEKRAfolder, boolean overwrite) throws IOException {
		File f = new File(superpath + "/" + fileInINEKRAfolder);
		if(!f.exists()){
			File parent = f.getParentFile();
			if(!parent.exists())
				parent.mkdirs();
			f.createNewFile();
		}
		return new BufferedWriter(new FileWriter(f, !overwrite));
	}

}
