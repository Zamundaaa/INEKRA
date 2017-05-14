package textures;

public class TerrainTexturePack {

	private TerrainTexture backtex;
	private TerrainTexture rtex;
	private TerrainTexture gtex;
	private TerrainTexture btex;

	public TerrainTexturePack(TerrainTexture backtex, TerrainTexture rtex, TerrainTexture gtex, TerrainTexture btex) {
		super();
		this.backtex = backtex;
		this.rtex = rtex;
		this.gtex = gtex;
		this.btex = btex;
	}

	public TerrainTexture getBacktex() {
		return backtex;
	}

	public TerrainTexture getRtex() {
		return rtex;
	}

	public TerrainTexture getGtex() {
		return gtex;
	}

	public TerrainTexture getBtex() {
		return btex;
	}
}
