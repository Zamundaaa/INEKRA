package renderStuff;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import entities.Entity;
import entities.MWBE;
import gameStuff.TM;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolBox.Meth;
import toolBox.Vects;
import weather.WeatherController;

public class EntityRenderer {

	private StaticShader shader;

	public static boolean WIREFRAME = false;

	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<MWBE>> entities, Matrix4f toShadowSpace) {
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		shader.loadGradient(WeatherController.getFogGradient());
		shader.loadDensity(WeatherController.getFogDensity());
		if (MasterRenderer.SHADOWS && TM.isDay()) {
			shader.loadToShadowMapSpaceMatrix(toShadowSpace);
			shader.loadShadow(true);
		} else {
			shader.loadShadow(false);
		}
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<MWBE> batch = entities.get(model);
			for (MWBE entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawMod().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawMod();
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		
		
		ModelTexture tex = model.getTex();
		shader.loadNOR(tex.getNOR());
		if (tex.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLightningVariable(tex.isUseFakeLightning());
		shader.loadShineVariables(tex.getShineDamper(), tex.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTex().getID());
		shader.loadUseSpecularMap(tex.hasSpecularMap());
		if (tex.hasSpecularMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getSpecularMap());
		}
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		
		
		
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(MWBE entity) {
		Matrix4f transformationMatrix = Meth.createTransformationMatrix(entity.getX(), entity.getY(), entity.getZ(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale(), Vects.mat4);
		shader.loadTransformationMatrix(transformationMatrix);
		// shader.loadHighlight(entity.highlighted());
		shader.loadOffSet(entity.getTextureXOffset(), entity.getTextureYOffset());
	}

	public void render(Map<TexturedModel, List<Entity>> entities) {
		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		shader.loadGradient(WeatherController.getFogGradient());
		shader.loadDensity(WeatherController.getFogDensity());
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawMod().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}

		if (WIREFRAME)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

}
