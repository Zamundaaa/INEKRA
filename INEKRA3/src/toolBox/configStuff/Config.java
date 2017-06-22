package toolBox.configStuff;

import java.util.*;

import toolBox.Tools;

public class Config {
	
	private final Map<String, String> stringConfigs = new HashMap<>();
	private final Map<String, Integer> intConfigs = new HashMap<>();
	private final Map<String, Long> longConfigs = new HashMap<>();
	private final Map<String, Float> floatConfigs = new HashMap<>();
	private final Map<String, Double> doubleConfigs = new HashMap<>();
	private final Map<String, Boolean> boolConfigs = new HashMap<>();
	
	public final String pathInsideGameFolder;
	private int entrys;
	
	public Config(String pathInsideThingsFolder){
		this.pathInsideGameFolder = pathInsideThingsFolder;
		load(false);
	}

	public Config(String pathInsideThingsFolder, boolean createFileIfNotPresent) {
		this.pathInsideGameFolder = pathInsideThingsFolder;
		load(createFileIfNotPresent);
	}

	public int getIntConfig(String name){
		return intConfigs.get(name);
	}
	
	public long getLongConfig(String name){
		return longConfigs.get(name);
	}
	
	public float getFloatConfig(String name){
		return floatConfigs.get(name);
	}
	
	public double getDoubleConfig(String name){
		return doubleConfigs.get(name);
	}

	public boolean getBoolConfig(String name) {
		return boolConfigs.get(name);
	}
	
	public String getConfig(String name){
		return stringConfigs.get(name);
	}
	
	public void setConfig(String name, String conf){
		stringConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void setConfig(String name, int conf){
		intConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void setConfig(String name, long conf){
		longConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void setConfig(String name, float conf){
		floatConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void setConfig(String name, double conf){
		doubleConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void setConfig(String name, boolean conf){
		boolConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void load(boolean createFileIfNotPresent){
		String content = Tools.readFile(pathInsideGameFolder);
		if(content == null){
//			System.err.println("The configuration file '" + Tools.getFolderPath() + "/" + pathInsideGameFolder + "' doesn't exist!");
			Tools.writeToFile(pathInsideGameFolder, "No Content Yet!");
			return;
		}
		stringConfigs.clear();
		intConfigs.clear();
		longConfigs.clear();
		boolConfigs.clear();
		floatConfigs.clear();
		doubleConfigs.clear();
//		System.out.println("-----------------------------");
//		System.out.println(content);
//		System.out.println("-----------------------------");
		String[] configs = content.split("\n");
		for(int i = 0; i < configs.length; i++){
			if(!configs[i].isEmpty()){
				String trimmed = configs[i].trim();
//				System.out.println(trimmed);
				if(trimmed.endsWith("{")){
//					System.out.println("X: '" + trimmed.substring(0, trimmed.length()-1) + "'");
					i++;
					switch(trimmed.substring(0, trimmed.length()-1)){
					case "Strings":
						for(; i < configs.length && !configs[i].startsWith("}"); i++){
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							stringConfigs.put(cfgs[0], cfgs[1]);
						}
						break;
					case "Integers":
						for(; i < configs.length && !configs[i].startsWith("}"); i++){
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							intConfigs.put(cfgs[0], Integer.parseInt(cfgs[1]));
//							System.out.println(cfgs[0] + " -- " + cfgs[1]);
						}
						break;
					case "Longs":
						for(; i < configs.length && !configs[i].startsWith("}"); i++){
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							longConfigs.put(cfgs[0], Long.parseLong(cfgs[1]));
						}
						break;
					case "Floats":
						for(; i < configs.length && !configs[i].startsWith("}"); i++){
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							floatConfigs.put(cfgs[0], Float.parseFloat(cfgs[1]));
						}
						break;
					case "Doubles":
						for(; i < configs.length && !configs[i].startsWith("}"); i++){
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							doubleConfigs.put(cfgs[0], Double.parseDouble(cfgs[1]));
						}
						break;
					case "Booleans":
						for(; i < configs.length && !configs[i].startsWith("}"); i++){
							for(; i < configs.length && !configs[i].startsWith("}"); i++){
								String[] cfgs = configs[i].split("=");
								cfgs[0] = cfgs[0].trim();
								cfgs[1] = cfgs[1].trim();
								boolConfigs.put(cfgs[0], Boolean.parseBoolean(cfgs[1]));
							}
						}
					}
					//i++
				}
			}
		}
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size() + doubleConfigs.size() + boolConfigs.size();
	}
	
	public void save(){
		Tools.writeToFile(pathInsideGameFolder, getConfigString());
	}
	
	public String getConfigString(){
		StringBuilder context = new StringBuilder();
		if(stringConfigs.size() > 0){
			context.append("Strings{\n");
			for(String key : stringConfigs.keySet()){
				context.append(key);
				context.append(" = ");
				context.append(stringConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if(intConfigs.size() > 0){
			context.append("Integers{\n");
			for(String key : intConfigs.keySet()){
				context.append(key);
				context.append(" = ");
				context.append(intConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if(longConfigs.size() > 0){
			context.append("Longs{\n");
			for(String key : longConfigs.keySet()){
				context.append(key);
				context.append(" = ");
				context.append(longConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if(floatConfigs.size() > 0){
			context.append("Floats{\n");
			for(String key : floatConfigs.keySet()){
				context.append(key);
				context.append(" = ");
				context.append(floatConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if(doubleConfigs.size() > 0){
			context.append("Doubles{\n");
			for(String key : doubleConfigs.keySet()){
				context.append(key);
				context.append(" = ");
				context.append(doubleConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if(boolConfigs.size() > 0){
			context.append("Booleans{\n");
			for(String key : boolConfigs.keySet()){
				context.append(key);
				context.append(" = ");
				context.append(boolConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		return context.toString();
	}

	public int entryCount() {
		return entrys;
	}

	public boolean isKey(String name) {
		return stringConfigs.keySet().contains(name) || intConfigs.keySet().contains(name)
				|| longConfigs.keySet().contains(name) || floatConfigs.keySet().contains(name)
				|| doubleConfigs.keySet().contains(name) || boolConfigs.keySet().contains(name);
	}
	
	@Override
	public String toString(){
		return "Config of the file " + pathInsideGameFolder + "\n" + getConfigString();
	}
	
}
