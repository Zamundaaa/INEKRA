package shadows;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import blockRendering.BlockRenderer;
import blockRendering.ChunkEntity;
import entities.MWBE;
import models.RawModel;
import models.TexturedModel;
import renderStuff.MasterRenderer;
import toolBox.Meth;
import toolBox.Vects;

public class ShadowMapEntityRenderer {

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	/**
	 * @param shader
	 *            - the simple shader program being used for the shadow render
	 *            pass.
	 * @param projectionViewMatrix
	 *            - the orthographic projection matrix multiplied by the light's
	 *            "view" matrix.
	 */
	protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Renders entities to the shadow map. Each model is first bound and then
	 * all of the entities using that model are rendered to the shadow map.
	 * 
	 * @param entities
	 *            - the entities to be rendered to the shadow map.
	 * @param ces
	 */
	protected void render(Map<TexturedModel, List<MWBE>> entities) {
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawMod();
			bindModel(rawModel);
			// if (entities.get(model).get(0).getClass() == ChunkEntity.class) {
			// GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTex().getID());
			// } else {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTex().getID());
			// }
			if (model.getTex().isHasTransparency()) {
				MasterRenderer.disableCulling();
			}
			for (MWBE entity : entities.get(model)) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			if (model.getTex().isHasTransparency()) {
				MasterRenderer.enableCulling();
			}
		}
		
		
		// 
		GL30.glBindVertexArray(0);
	}

	protected void renderChunks(List<ChunkEntity> ces) {
		BlockRenderer.bindTex();
		shader.loadMvpMatrix(projectionViewMatrix);
		BlockRenderer.renderForShadowMap();
//		for (int i = 0; i < ces.size(); i++) {
//			GL30.glBindVertexArray(ces.get(i).getMod().getVaoID());
//			
//			
//			MasterRenderer.disableCulling();
//			prepareInstance(ces.get(i));
//			GL11.glDrawElements(GL11.GL_TRIANGLES, BlockRenderer.getIndices(), GL11.GL_UNSIGNED_INT, 0);
//			
//			
//		}
//		GL30.glBindVertexArray(0);
	}

	/**
	 * Binds a raw model before rendering. Only the attribute 0 is enabled here
	 * because that is where the positions are stored in the VAO, and only the
	 * positions are required in the vertex shader.
	 * 
	 * @param rawModel
	 *            - the model to be bound.
	 */
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		
		// 
	}

	/**
	 * Prepares an entity to be rendered. The model matrix is created in the
	 * usual way and then multiplied with the projection and view matrix (often
	 * in the past we've done this in the vertex shader) to create the
	 * mvp-matrix. This is then loaded to the vertex shader as a uniform.
	 * 
	 * @param entity
	 *            - the entity to be prepared for rendering.
	 */
	private void prepareInstance(MWBE entity) {
		Matrix4f modelMatrix = Meth.createTransformationMatrix(entity.getX(), entity.getY(), entity.getZ(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale(), Vects.mat4);
		projectionViewMatrix.mul(modelMatrix, Vects.mat4);
		shader.loadMvpMatrix(Vects.mat4);
	}

}
