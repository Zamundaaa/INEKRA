package models;

import textures.ModelTexture;

public class TexturedModel {

	private RawModel rawMod;
	private ModelTexture tex;

	public TexturedModel(RawModel mod, ModelTexture tex) {
		rawMod = mod;
		this.tex = tex;
	}

	public RawModel getRawMod() {
		return rawMod;
	}

	public ModelTexture getTex() {
		return tex;
	}

	public void setRawMod(RawModel mod) {
		this.rawMod = mod;
	}

	public void setTexture(ModelTexture tex) {
		this.tex = tex;
	}
}
