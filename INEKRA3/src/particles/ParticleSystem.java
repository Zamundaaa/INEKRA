package particles;

import java.util.ArrayList;

import toolBox.Meth;
import toolBox.Tool;

public class ParticleSystem {

	private static ArrayList<ParticleTexture> tex = new ArrayList<ParticleTexture>();
	private float rate;
	private long lastTime = 0;
	private Tool posTool, velTool, sizeTool, lifeTool, gravTool;

	public ParticleSystem(ParticleTexture first, Tool posTool, Tool velTool, Tool sizeTool, Tool lifeTool,
			Tool gravityTool, float rate) {
		if (!tex.contains(first)) {
			tex.add(first);
		}
		this.posTool = posTool;
		this.velTool = velTool;
		this.sizeTool = sizeTool;
		this.lifeTool = lifeTool;
		this.gravTool = gravityTool;
		this.rate = rate;
	}

	public void setPosTool(Tool posTool) {
		this.posTool = posTool;
	}

	public static void setTex(ArrayList<ParticleTexture> tex) {
		ParticleSystem.tex = tex;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public void setVelTool(Tool velTool) {
		this.velTool = velTool;
	}

	public void setSizeTool(Tool sizeTool) {
		this.sizeTool = sizeTool;
	}

	public void setLifeTool(Tool lifeTool) {
		this.lifeTool = lifeTool;
	}

	public void setGravTool(Tool gravTool) {
		this.gravTool = gravTool;
	}

	public void update() {
		if (System.currentTimeMillis() - lastTime > rate * 1000) {
			lastTime = System.currentTimeMillis();
			ParticleMaster.addNewParticle(tex.get(Meth.randomInt(0, tex.size() - 1)), posTool.returnCustomVect(),
					velTool.returnCustomVect(), gravTool.returnCustomFloat(), lifeTool.returnCustomFloat(), 0,
					sizeTool.returnCustomFloat());
		}
	}

	public void addRandomParticle(ParticleTexture text) {
		if (!tex.contains(text)) {
			tex.add(text);
		}
	}

	public void removeRandomParticle(ParticleTexture text) {
		tex.remove(text);
	}

}
