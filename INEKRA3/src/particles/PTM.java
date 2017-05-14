package particles;

import data.Block;
import renderStuff.Loader;
import textureGenerator.CloudRenderer;

public class PTM {

	public static void init() {
		fire = new ParticleTexture(Loader.loadParticleTexture("fire"), 8);
		projectile = new ParticleTexture(Loader.loadParticleTexture("particleAtlas"), 4);
		sand = new ParticleTexture(Loader.loadParticleTexture("particleAtlas"), 4);
		cosmic = new ParticleTexture(Loader.loadParticleTexture("cosmic"), 4);
		lightning = new ParticleTexture(Loader.loadParticleTexture("cosmic"), 4);
		star = new ParticleTexture(Loader.loadParticleTexture("particleStar"), 1);
		raindrop = new ParticleTexture(Loader.loadParticleTexture("raindrop"), 1);
		snowflake = new ParticleTexture(Loader.loadParticleTexture("snowflake"), 8);
		white = new ParticleTexture(Loader.loadParticleTexture("white"), 1);
		herbstblatt = new ParticleTexture(Loader.loadParticleTexture("herbstblatt"), 1);
		frühlingsblatt = new ParticleTexture(Loader.loadParticleTexture("frühlingsblatt"), 2);
		cloudy = new ParticleTexture(CloudRenderer.getCloudTexture(1, Block.GLASS), 1);
		cloudy2 = new ParticleTexture(CloudRenderer.getCloudTexture(0.9f, Block.FROZEN_GRASS), 1);
		cloudy3 = new ParticleTexture(CloudRenderer.getCloudTexture(0.5f, Block.BROWN_LEAVES), 1);
		// cloudy5 = new ParticleTexture(Loader.loadParticleTexture("cloudy5"),
		// 1);
		stony = new ParticleTexture(Loader.loadParticleTexture("grey"), 4);
		fireworks = new ParticleTexture(Loader.loadParticleTexture("fireworks"), 4);
		fireworks.setBright(10);
		// lightning.setBright(100);
		// fire.setBright(100);
		// projectile.setBright(10);
		// fireworks.setBright(1);
		lightning.setBright(100);
		fire.setBright(1);
		projectile.setBright(10);
		cloudy.setBright(1);
		cloudy2.setBright(1);
		cloudy3.setBright(1);
		// cloudy5.setBright(1);

		cloudy.setTimeDarkening(true);
		cloudy2.setTimeDarkening(true);
		cloudy3.setTimeDarkening(true);
		// cloudy5.setTimeDarkening(true);
	}

	public static ParticleTexture fire;
	public static ParticleTexture projectile;
	public static ParticleTexture sand;
	public static ParticleTexture cosmic;
	public static ParticleTexture star;
	public static ParticleTexture raindrop;
	public static ParticleTexture snowflake;
	public static ParticleTexture white;
	public static ParticleTexture lightning;
	public static ParticleTexture herbstblatt;
	public static ParticleTexture frühlingsblatt;
	public static ParticleTexture cloudy, cloudy2;
	public static ParticleTexture cloudy3;
	// public static ParticleTexture cloudy5;

	public static ParticleTexture stony;
	public static ParticleTexture fireworks;

	// public static ParticleTexture l = new
	// ParticleTexture(Loader.loadParticleTexture("l"), 2);

}
