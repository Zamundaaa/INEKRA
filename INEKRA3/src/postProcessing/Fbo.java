
package postProcessing;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import renderStuff.DisplayManager;
import renderStuff.Loader;

public class Fbo {

	public static final int NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_RENDER_BUFFER = 2;
	public static final int CUBEMAP = 3;
	public static final int HDR = 4;
	public static final int ONLYMULTISAMPLEDWITHDEPTHTEXTURE = 5;

	private final int width;
	private final int height;

	private int frameBuffer;

	private int colourTexture;
	private int depthTexture;

	private boolean multisampleAndMultiTargets = false;

	private int depthBuffer;
	private int colourBuffer;
	private int colourBuffer2;

	/**
	 * Creates an FBO of a specified width and height, with the desired type of
	 * depth buffer attachment.
	 * 
	 * @param width
	 *            - the width of the FBO.
	 * @param height
	 *            - the height of the FBO.
	 * @param type
	 *            - an int indicating the type of (depth buffer) attachment that
	 *            this FBO should use.
	 */
	public Fbo(int width, int height, int type) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer(type);
	}

	public Fbo(int width, int height) {
		this.width = width;
		this.height = height;
		this.multisampleAndMultiTargets = true;
		initialiseFrameBuffer(DEPTH_RENDER_BUFFER);
	}

	/**
	 * @param size
	 *            creates a Fbo with a CubeMapTexture of size size
	 */
	public Fbo(int size) {
		this.width = size;
		this.height = size;
		initialiseFrameBuffer(CUBEMAP);
	}

	/**
	 * Deletes the frame buffer and its attachments when the game closes.
	 */
	public void cleanUp() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colourTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
		GL30.glDeleteRenderbuffers(colourBuffer);
		GL30.glDeleteRenderbuffers(colourBuffer2);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target. Anything
	 * rendered after this will be rendered to this FBO, and not to the screen.
	 */
	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		// DisplayManager.setViewPort();
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target. Anything rendered after this will be rendered to the
	 * screen, and not this FBO.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		DisplayManager.setViewPort();
	}

	/**
	 * Binds the current FBO to be read from (not used in tutorial 43).
	 */
	public void bindToRead() {
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	/**
	 * @return The ID of the texture containing the colour buffer of the FBO.
	 */
	public int getColourTexture() {
		return colourTexture;
	}

	/**
	 * @return The texture containing the FBOs depth buffer.
	 */
	public int getDepthTexture() {
		return depthTexture;
	}

	public void resolveToFbo(int readBuffer, Fbo outputFbo) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glReadBuffer(readBuffer);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height,
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbindFrameBuffer();
	}

	public int resolveToNewTexture(int readBuffer) {
		int tex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glReadBuffer(readBuffer);
//		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height,
//				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, 0, 0, width, height, 0);
		this.unbindFrameBuffer();
		Loader.applyFilterAndCreateMipMaps(tex, GL11.GL_LINEAR);
		return tex;
	}

	public void resolveToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, DisplayManager.getWidth(), DisplayManager.getHeight(),
				GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbindFrameBuffer();
	}

	/**
	 * Creates the FBO along with a colour buffer texture attachment, and
	 * possibly a depth buffer.
	 * 
	 * @param type
	 *            - the type of (depth buffer) attachment to be attached to the
	 *            FBO.
	 */
	private void initialiseFrameBuffer(int type) {
		createFrameBuffer();
		if (multisampleAndMultiTargets) {
			colourBuffer = createMultisampleColorAttatchment(GL30.GL_COLOR_ATTACHMENT0);
			colourBuffer2 = createMultisampleColorAttatchmentHDR(GL30.GL_COLOR_ATTACHMENT1);
		} else {
			if (type == HDR) {
				createTextureAttachmentHDR();
			} else if (type == CUBEMAP) {
				createCubeMapTexture();
			} else if(type == ONLYMULTISAMPLEDWITHDEPTHTEXTURE){
				createMultisampleColorAttatchment(GL30.GL_COLOR_ATTACHMENT0);
			} else {
				createTextureAttachment();
			}
		}
		if (type == DEPTH_RENDER_BUFFER || type == CUBEMAP) {
			createDepthBufferAttachment(multisampleAndMultiTargets || type == ONLYMULTISAMPLEDWITHDEPTHTEXTURE);
//		} else if (type == ONLYMULTISAMPLEDWITHDEPTHTEXTURE){
//			createDepthBufferAttachmentMultisampled();
		} else if (type == DEPTH_TEXTURE) {
			createDepthTextureAttachment();
		}
		unbindFrameBuffer();
	}

	/**
	 * Creates a new frame buffer object and sets the buffer to which drawing
	 * will occur - colour attachment 0. This is the attachment where the colour
	 * buffer texture is.
	 */
	private void createFrameBuffer() {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		determineDrawBuffers();
	}

	private void determineDrawBuffers() {
		IntBuffer drawBuffers = BufferUtils.createIntBuffer(2);
		drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
		if (this.multisampleAndMultiTargets) {
			drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1);
		}
		drawBuffers.flip();
		GL20.glDrawBuffers(drawBuffers);
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private void createTextureAttachment() {
		colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture,
				0);
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private void createTextureAttachmentHDR() {
		colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture,
				0);
	}

	private void createCubeMapTexture() {
		colourTexture = Loader.createEmptyCubeMap(width);
//		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL13.GL_TEXTURE_CUBE_MAP,
//				colourTexture, 0);
//		GL30.glFramebufferTexture3D(GL30.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE, GL30.GL_COLOR_ATTACHMENT0, textarget, texture, level, layer);
	}

	/**
	 * only for cubemaps!
	 * @param side GL13.GL_TEXTURE_CUBE_MAP_XXXXXXXXX
	 */
	public void createSideForRendering(int side) {
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, side, colourTexture, 0);
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a texture, which can later
	 * be sampled.
	 */
	private void createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT,
				GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}

	public int createMultisampleColorAttatchment(int attachment) {
		int colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, colourBuffer);
		return colourBuffer;
	}

	public int createMultisampleColorAttatchmentHDR(int attachment) {
		int colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL30.GL_RGBA16F, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, colourBuffer);
		return colourBuffer;
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a render buffer. This can't
	 * be used for sampling in the shaders.
	 */
	private void createDepthBufferAttachment(boolean multiSampled) {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if (!multiSampled) {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		} else {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, width, height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
	}

	private static float[] depthClear = new float[]{1}, colorClear = new float[]{0, 0, 0, 0};
	
	/**
	 * BIND IT FIRST!!!
	 */
	public void clearColor1AndDepthBuffer(){
		GL30.glClearBufferfv(GL11.GL_DEPTH, 0, depthClear);
		GL30.glClearBufferfv(GL11.GL_COLOR, 0, colorClear);
	}

	/**
	 * BIND IT FIRST!!!
	 * CLEARS Color Buffer 0, 1 and the DepthBuffer
	 */
	public void clearMultisampled() {
		GL30.glClearBufferfv(GL11.GL_DEPTH, 0, depthClear);
		GL30.glClearBufferfv(GL11.GL_COLOR, 0, colorClear);
		GL30.glClearBufferfv(GL11.GL_COLOR, 1, colorClear);
	}

}
