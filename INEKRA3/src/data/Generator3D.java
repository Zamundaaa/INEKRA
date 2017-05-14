package data;

import java.util.Random;

public class Generator3D {

	public static final long seed = Generator.seed;
	private static Generator3D G = new Generator3D();
	private static final Random random = new Random();

	public static Generator3D getGen() {
		return G;
	}

	private static final float AMPLITUDE = 1;
	private static final int OCTAVES = 2;
	private static final float ROUGHNESS = 0.3f;

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return range: -1 bis 1
	 */
	public double getDensity(double x, double y, double z) {
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES - 1);
		float amp = AMPLITUDE;
		for (int i = 0; i < OCTAVES; i++) {
			amp *= ROUGHNESS;
			float freq = (float) (Math.pow(2, i) / d);
			total += getInterpolatedNoise(x * freq, y * freq, z * freq) * amp;
		}
		return total;
	}

	public double getInterpolatedNoise(double x, double y, double z) {
		int intX = (int) x;
		int intY = (int) y;
		int intZ = (int) z;
		double fracX = x - intX;
		double fracY = y - intY;
		double fracZ = z - intZ;

		double v1 = getSmoothNoise(intX, intY, intZ); // luv
		double v2 = getSmoothNoise(intX + 1, intY, intZ); // ruv
		double v3 = getSmoothNoise(intX, intY, intZ + 1); // luh
		double v4 = getSmoothNoise(intX + 1, intY, intZ + 1);// ruh
		double v5 = getSmoothNoise(intX, intY + 1, intZ); // lov
		double v6 = getSmoothNoise(intX + 1, intY + 1, intZ); // rov
		double v7 = getSmoothNoise(intX, intY + 1, intZ + 1); // loh
		double v8 = getSmoothNoise(intX + 1, intY + 1, intZ + 1);// roh
		double d1 = interpolate(v1, v2, fracX);
		double d2 = interpolate(v3, v4, fracX);
		double d = interpolate(d1, d2, fracZ);
		double u1 = interpolate(v5, v6, fracX);
		double u2 = interpolate(v7, v8, fracX);
		double u = interpolate(u1, u2, fracZ);
		return interpolate(d, u, fracY);
	}

	public double getSmoothNoise(int x, int y, int z) {
		double ret = (noise(x + 1, y, z) + noise(x - 1, y, z) + noise(x, y + 1, z) + noise(x, y - 1, z)
				+ noise(x, y, z + 1) + noise(x, y, z - 1)) / 12;
		ret += noise(x, y, z) / 2;
		return ret;
	}

	public double noise(int x, int y, int z) {
		random.setSeed(x * 34218 + y * 45312 + z * 321);
		return random.nextDouble();
	}

	private double interpolate(double v1, double v2, double fracX) {
		double theta = fracX * Math.PI;
		double f = (1f - Math.cos(theta)) * 0.5;
		return v1 * (1f - f) + v2 * f;
	}

}
