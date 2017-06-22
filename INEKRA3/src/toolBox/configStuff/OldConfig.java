package toolBox.configStuff;

import java.util.*;

import toolBox.Tools;

public class OldConfig {
	
	private final Map<String, String> configurations = new HashMap<String, String>();
	public final String pathInsideThingsFolder;
	private int entrys;
	
	public OldConfig(String pathInsideThingsFolder){
		this.pathInsideThingsFolder = pathInsideThingsFolder;
		String content = Tools.readFile(pathInsideThingsFolder);
		if(content == null)return;
		String[] configs = content.split("\n");
		for(int i = 0; i < configs.length; i++){
			if(!configs[i].isEmpty()){
				String[] cfgs = configs[i].split("=");
				cfgs[0] = cfgs[0].trim();
				cfgs[1] = cfgs[1].trim();
				configurations.put(cfgs[0], cfgs[1]);
			}
		}
		entrys = configurations.size();
	}
	
	public int readIntConfig(String name){
		return Integer.parseInt(configurations.get(name));
	}
	
	public long readLongConfig(String name){
		return Long.parseLong(configurations.get(name));
	}
	
	public float readFloatConfig(String name){
		return Float.parseFloat(configurations.get(name));
	}
	
	public double readDoubleConfig(String name){
		return Double.parseDouble(configurations.get(name));
	}
	
	public String getConfig(String name){
		return configurations.get(name);
	}
	
	public void setConfig(String name, String conf){
		configurations.put(name, conf);
		entrys = configurations.size();
	}
	
	public void setConfig(String name, int conf){
		configurations.put(name, "" + conf);
		entrys = configurations.size();
	}
	
	public void setConfig(String name, long conf){
		configurations.put(name, "" + conf);
		entrys = configurations.size();
	}
	
	public void setConfig(String name, float conf){
		configurations.put(name, "" + conf);
		entrys = configurations.size();
	}
	
	public void setConfig(String name, double conf){
		configurations.put(name, "" + conf);
		entrys = configurations.size();
	}
	
	public void reload(){
		String content = Tools.readFile(pathInsideThingsFolder);
		if(content == null)return;
		configurations.clear();
		String[] configs = content.split("\n");
		for(int i = 0; i < configs.length; i++){
			if(!configs[i].isEmpty()){
				String[] cfgs = configs[i].split("=");
				cfgs[0] = cfgs[0].trim();
				cfgs[1] = cfgs[1].trim();
				configurations.put(cfgs[0], cfgs[1]);
			}
		}
		entrys = configurations.size();
	}
	
//	public void save(){
//		new BetterConfig(this).save();
//	}
	
//	@Override
//	public String toString(){
//		StringBuilder context = new StringBuilder();
//		for(String key : configurations.keySet()){
//			context.append(key);
//			context.append(" = ");
//			context.append(configurations.get(key));
//			context.append('\n');
//		}
//		return context.toString();
//	}

	public int entryCount() {
		return entrys;
	}
	
	public Set<String> keySet(){
		return configurations.keySet();
	}
	
}
