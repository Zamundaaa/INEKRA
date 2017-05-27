package toolBox;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.lwjgl.BufferUtils;

import gameStuff.Err;

public class PIC {

	public static BufferedImage loadImage(String path) {
		BufferedImage ret = null;
		try {
			ret = ImageIO.read(PIC.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace(Err.err);
			Err.err.println("failed to Load");
		}
		return ret;
	}

	public static BufferedImage loadImage(String path, int width, int height) {
		BufferedImage ret = loadImage(path);
		return scale(ret, width, height);
	}

	public static BufferedImage scale(BufferedImage alt, int neux, int neuy) {
		if (alt == null) {
			return null;
		} else {
			BufferedImage neu = new BufferedImage(neux, neuy, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) neu.getGraphics();
			g2.drawImage(alt, 0, 0, neux, neuy, null);
			g2.dispose();
			return neu;
		}
	}

	/**
	 * @param img
	 * @param folder
	 * @throws IOException
	 * @comment naja. Funzt so wies is nur in Eclipse!
	 */
	public static void saveFile(BufferedImage img, String folder) throws IOException {
		File Ordner = new File(System.getProperty("user.dir") + "/src/" + folder);
		int number;
		if (Ordner.list() == null) {
			number = 0;
		} else {
			number = Ordner.list().length;
		}
		File Bild = new File(System.getProperty("user.dir") + "/src/" + folder + "/Bild" + number + ".png");
		Bild.createNewFile();
		ImageIO.write(img, "png", Bild);
	}

	public static ByteBuffer loadImg(String path) {
		return imgToBuffer(loadImage(path));
	}

	public static ByteBuffer loadImg(String path, int w, int h) {
		return imgToBuffer(loadImage(path, w, h));
	}

	public static void flipImage(BufferedImage img) {
		AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
		transform.translate(0, -img.getHeight());
		AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img = operation.filter(img, null);
	}

	public static ByteBuffer imgToBuffer(BufferedImage img) {

		int width = img.getWidth();
		int height = img.getHeight();

		int[] pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(height * width * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				/* Pixel as RGBA: 0xAARRGGBB */
				int pixel = pixels[y * width + x];

				/* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
				buffer.put((byte) ((pixel >> 16) & 0xFF));

				/* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
				buffer.put((byte) ((pixel >> 8) & 0xFF));

				/* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
				buffer.put((byte) (pixel & 0xFF));

				/* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		/* Do not forget to flip the buffer! */
		buffer.flip();

		return buffer;
	}

	public static Dimension getDimensions(String file) {
		try(ImageInputStream in = ImageIO.createImageInputStream(PIC.class.getClassLoader().getResourceAsStream(file))){
		    final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
		    if (readers.hasNext()) {
		        ImageReader reader = readers.next();
		        try {
		            reader.setInput(in);
		            return new Dimension(reader.getWidth(0), reader.getHeight(0));
		        } finally {
		            reader.dispose();
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
			Err.err.println(file);
		}
		return null;
	}

	// public static BufferedImage BufferToImg(FloatBuffer imageData, int w, int
	// h) {
	// BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	// ret.setRGB(startX, startY, w, h, rgbArray, offset, scansize);
	// return null;
	// }

	// public static ByteBuffer imgToBuffer(BufferedImage img){
	// ByteBuffer byteBuffer;
	// DataBuffer dataBuffer = img.getRaster().getDataBuffer();
	//
	// if (dataBuffer instanceof DataBufferByte) {
	// byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
	// byteBuffer = ByteBuffer.wrap(pixelData);
	// }
	// else if (dataBuffer instanceof DataBufferUShort) {
	// short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
	// byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
	// byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
	// }
	// else if (dataBuffer instanceof DataBufferShort) {
	// short[] pixelData = ((DataBufferShort) dataBuffer).getData();
	// byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
	// byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
	// }
	// else if (dataBuffer instanceof DataBufferInt) {
	// int[] pixelData = ((DataBufferInt) dataBuffer).getData();
	// byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
	// byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
	// }
	// else {
	// throw new IllegalArgumentException("Not implemented for data buffer type:
	// " + dataBuffer.getClass());
	// }
	//// byteBuffer.flip();
	// return byteBuffer;
	// }

}
