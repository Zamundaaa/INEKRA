package mobs;

import org.joml.Vector3f;

import data.*;
import entities.Camera;
import renderStuff.DisplayManager;
import toolBox.Meth;
import weather.WeatherController;

public class MobMaster {

	public static boolean SPAWNMOBS = false;

	public static void update() {
		if (SPAWNMOBS && Meth.doChance(DisplayManager.getFrameTimeSeconds())) {
			float x = Camera.getPosition().x + Meth.randomFloat(-Chunk.SIZE * 2, Chunk.SIZE * 2);
			float y = Camera.getPosition().y + Meth.randomFloat(-Chunk.SIZE * 2, Chunk.SIZE * 2);
			float z = Camera.getPosition().z + Meth.randomFloat(-Chunk.SIZE * 2, Chunk.SIZE * 2);
			short b = ChunkManager.getBlockID(x, y, z);
			if (b == Block.AIR) {
				short bu = ChunkManager.getBlockID(x, y - 1, z);
				if (bu != Block.AIR) {
					Vector3f pos = new Vector3f(x, y, z);
					WeatherController.lstrikeback(pos);
					new Cube(pos);
				}
			}
		}
	}

}
