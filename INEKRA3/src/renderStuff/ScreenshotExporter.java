package renderStuff;

import static renderStuff.DisplayManager.HEIGHT;
import static renderStuff.DisplayManager.WIDTH;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;

import javax.imageio.ImageIO;

import gameStuff.Err;
import gameStuff.MainLoop;
import toolBox.Meth;
import toolBox.Tools;

public class ScreenshotExporter {

	private static ArrayDeque<FloatBuffer> queue = new ArrayDeque<>();
	private static ArrayDeque<File> q2 = new ArrayDeque<>();
	private static Thread exporter = new Thread() {
		@Override
		public void run() {
			while (MainLoop.alive) {
				while (queue.isEmpty()) {
					Meth.wartn(50);
				}
				while (!queue.isEmpty()) {// to export all the screenshots even
											// when the application is closed!
					FloatBuffer imageData = queue.poll();
					// fill rgbArray for BufferedImage
					int[] rgbArray = new int[WIDTH * HEIGHT];
					for (int y = 0; y < HEIGHT; ++y) {
						for (int x = 0; x < WIDTH; ++x) {
							int r = (int) (imageData.get() * 255) << 16;
							int g = (int) (imageData.get() * 255) << 8;
							int b = (int) (imageData.get() * 255);
							int i = ((HEIGHT - 1) - y) * WIDTH + x;
							rgbArray[i] = r + g + b;
						}
					}
					// create and save image
					BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
					image.setRGB(0, 0, WIDTH, HEIGHT, rgbArray, 0, WIDTH);
					File outputfile = q2.poll();
					try {
						ImageIO.write(image, "png", outputfile);
						Err.err.println("Screenshot saved in " + Tools.screenShotFolder);
					} catch (IOException e) {
						Err.err.println("Could not save screenshot for any reason... see below!");
						e.printStackTrace(Err.err);
					}
				}
			}
		}
	};
	static {
		exporter.start();
	}

	public static void addToQueue(FloatBuffer imageData, File file) {
		queue.add(imageData);
		q2.add(file);
	}

}
