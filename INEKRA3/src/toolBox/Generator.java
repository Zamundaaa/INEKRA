package toolBox;

import java.util.Random;

public class Generator {

	// public static long seed = 213890;//third:86543;//second: 57743
	public static long seed = Meth.randomInt(0, 1000000000);
	private final Random random = new Random();
	private static final Generator THEBIGG = new Generator();

	public static final boolean CRAZY = false;

	public static Generator getG() {
		return THEBIGG;
	}

	public static final float AMPLITUDE = CRAZY ? 500 : 100;
	public static final int OCTAVES = 6;
	public static final float ROUGHNESS = 0.05f;

	public float getAmplitude(float x, float z) {
		if (CRAZY) {
			return AMPLITUDE;
		} else {
			return (float) (0.5f * AMPLITUDE * (Math.sin(x / 128.0f) + Math.cos(z / 256.0f)));// +
																								// Math.cos(x)
																								// //(genThing(x/16.0f,
																								// 4129,
																								// z/16.0f,
																								// 52189,
																								// 527)*AMPLITUDE)
		}
	}

	public float getRoughness(float x, float z) {
		if (CRAZY) {
			return 0.001f;
		} else {
			// return (genThing(x/4.0f, 415, z/4.0f, 1358, 527));
		}
		return 0.001f;
	}

	/**
	 * @param somevalue
	 * @return
	 */
	public float genThing(float somevalue) {
		int S = (int) somevalue;
		float fracS = somevalue - S;
		return interpolate(noise(S), noise(somevalue + 1), fracS);
	}

	public float noise(float somevalue) {
		random.setSeed((long) (seed * 421 + somevalue));
		return random.nextFloat() * 2f - 1;
	}

	// public float getProbability(int X, int Y, int Z){
	// float ret = (float)(Math.abs(generateHeight(X, Z) - (Y*TileChunk.SIZE)) /
	// AMPLITUDE);
	// return ret;
	// }

	public int getHeight(float x, float z) {
		return Meth.toInt(generateHeight(x, z));
	}

	public int getHeight(float x, float z, int number) {
		return Meth.toInt(generateHeight(x, z, number));
	}

	public float generateHeight(float x, float z) {
		float AMP = getAmplitude(x, z);
		float rough;
		// if(CRAZY){
		rough = getRoughness(x, z);
		// }
		// else{
		// rough = Math.abs(AMP/300.0f);
		// }

		float total = 0;
		float d = (float) Meth.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Meth.pow(2, i) / d);
			float amp = (float) Meth.pow(rough, i) * AMP;
			total += getInterpolatedNoise(x * freq, z * freq) * amp;
		}
		return total;
	}

	public float genThing(float x, float factx, float z, float factz, int number) {
		float total = 0;
		float d = (float) Meth.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Meth.pow(2, i) / d);
			float amp = (float) Meth.pow(ROUGHNESS, i);
			total += getInterpolatedNoise(x * freq, factx, z * freq, factz, number) * amp;
		}
		return total;
	}

	/**
	 * @param x
	 * @param z
	 * @param number
	 * @return range: -1 bis 1
	 */
	public float genThing(float x, float z, int number) {
		float total = 0;
		float d = (float) Meth.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Meth.pow(2, i) / d);
			float amp = (float) Meth.pow(ROUGHNESS, i);
			total += getInterpolatedNoise(x * freq, z * freq, number) * amp;
		}
		return total;
	}

	public float generateHeight(float x, float z, int number) {
		return genThing(x, z, number) * getAmplitude(x, z);
	}

	private float getInterpolatedNoise(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	private float getInterpolatedNoise(float x, float z, int number) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ, number);
		float v2 = getSmoothNoise(intX + 1, intZ, number);
		float v3 = getSmoothNoise(intX, intZ + 1, number);
		float v4 = getSmoothNoise(intX + 1, intZ + 1, number);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	private float getInterpolatedNoise(float x, float factx, float z, float factz, int number) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, factx, intZ, factz, number);
		float v2 = getSmoothNoise(intX + 1, factx, intZ, factz, number);
		float v3 = getSmoothNoise(intX, factx, intZ + 1, factz, number);
		float v4 = getSmoothNoise(intX + 1, factx, intZ + 1, factz, number);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracZ);
		return interpolate(i1, i2, fracZ);
	}

	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f - Math.cos(theta)) * 0.5f;
		return a * (1f - f) + b * f;
	}

	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
				+ getNoise(x + 1, z + 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}

	private float getSmoothNoise(int x, int z, int number) {
		float corners = (getNoise(x - 1, z - 1, number) + getNoise(x + 1, z - 1, number)
				+ getNoise(x - 1, z + 1, number) + getNoise(x + 1, z + 1, number)) / 16f;
		float sides = (getNoise(x - 1, z, number) + getNoise(x + 1, z, number) + getNoise(x, z - 1, number)
				+ getNoise(x, z + 1, number)) / 8f;
		float center = getNoise(x, z, number) / 4f;
		return corners + sides + center;
	}

	private float getSmoothNoise(int x, float factx, int z, float factz, int number) {
		float corners = (getNoise(x - 1, factx, z - 1, factz, number) + getNoise(x + 1, factx, z - 1, factz, number)
				+ getNoise(x - 1, factx, z + 1, factz, number) + getNoise(x + 1, factx, z + 1, factz, number)) / 16f;
		float sides = (getNoise(x - 1, factx, z, factz, number) + getNoise(x + 1, factx, z, factz, number)
				+ getNoise(x, factx, z - 1, factz, number) + getNoise(x, factx, z + 1, factz, number)) / 8f;
		float center = getNoise(x, factx, z, factz, number) / 4f;
		return corners + sides + center;
	}

	private float getNoise(int x, int z) {
		random.setSeed(x * 49632 + z * 325176 + seed);
		return random.nextFloat() * 2f - 1f;
	}

	private float getNoise(int x, int z, int number) {
		random.setSeed(x * (49632 + number) + z * (32576 + number) + seed);
		return random.nextFloat() * 2f - 1f;
	}

	private float getNoise(int x, float factx, int z, float factz, int number) {
		random.setSeed((long) (x * (factx + number) + z * (factz + number) + seed));
		return random.nextFloat() * 2f - 1f;
	}

}
