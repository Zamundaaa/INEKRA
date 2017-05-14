package gameStuff;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;

public class EntityManager {

	private static List<Entity> entities = new ArrayList<Entity>();

	public static List<Entity> getList() {
		return entities;
	}

	public static void addEntity(Entity e) {
		if (!entities.contains(e)) {
			entities.add(e);
		}
	}

	public static void removeEntity(Object e) {
		entities.remove(e);
	}

	public static void updateAll() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update();
		}
	}

	public static void cleanUp() {
		entities.clear();
	}

}
