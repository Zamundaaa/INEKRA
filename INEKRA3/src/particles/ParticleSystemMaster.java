package particles;

import static particles.PTM.fire;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import toolBox.*;

public class ParticleSystemMaster {

	private static List<ParticleSystem> systems = new ArrayList<ParticleSystem>();

	public static void addParticleSystem(ParticleSystem s) {
		systems.add(s);
	}

	public static void removeParticleSystem(ParticleSystem s) {
		systems.remove(s);
	}

	public static void update() {
		for (int i = 0; i < systems.size(); i++) {
			systems.get(i).update();
		}
	}

	public static ParticleSystem createFire(Vector3f position) {
		ParticleSystem sys = new ParticleSystem(fire, new Tool() {
			@Override
			public Vector3f returnCustomVect() {
				return new Vector3f(position.x + Meth.randomFloat(-1f, 1f), position.y + Meth.randomFloat(0, 3),
						position.z + Meth.randomFloat(-1f, 1f));
			}
		}, new Tool() {
			@Override
			public Vector3f returnCustomVect() {
				return Vects.randomVector3f(new Vector3f(-0.3f, 0.3f, -0.3f), new Vector3f(0.3f, 1, 0.3f));
			}
		}, new Tool() {
			@Override
			public float returnCustomFloat() {
				return Meth.randomFloat(5, 20);
			}
		}, new Tool() {
			@Override
			public float returnCustomFloat() {
				return Meth.randomFloat(1, 5);
			}
		}, new Tool() {
			@Override
			public float returnCustomFloat() {
				return -0.002f;
			}
		}, 0.01f);
		// sys.addRandomParticle(projectile);
		addParticleSystem(sys);
		new Thread() {
			@Override
			public void run() {
				float time = 0;
				while (time < 3000) {
					Meth.wartn(50);
					time += 50;
					float scopeTime = time;
					sys.setPosTool(new Tool() {
						@Override
						public Vector3f returnCustomVect() {
							return new Vector3f(position.x + Meth.randomFloat(-scopeTime / 200f, scopeTime / 200f),
									position.y + Meth.randomFloat(0, 2),
									position.z + Meth.randomFloat(-scopeTime / 200f, scopeTime / 200f));
						}
					});
					sys.setRate(1f / ((float) Math.PI * time / 50f));
				}
				removeParticleSystem(sys);
			}
		}.start();
		return sys;
	}

	public static ParticleSystem createTorchFire(Vector3f position) {
		// ParticleSystem sys = new ParticleSystem(0.1f, 1f, 2f, 3f, -0.07f);
		ParticleSystem sys1 = new ParticleSystem(fire, new Tool() {
			@Override
			public Vector3f returnCustomVect() {
				return position;
			}
		}, new Tool() {
			@Override
			public Vector3f returnCustomVect() {
				return Vects.randomVector3f(new Vector3f(-0.5f, 0, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f));
			}
		}, new Tool() {
			@Override
			public float returnCustomFloat() {
				return Meth.randomFloat(0.1f, 1);
			}
		}, new Tool() {
			@Override
			public float returnCustomFloat() {
				return Meth.randomFloat(2, 3);
			}
		}, new Tool() {
			@Override
			public float returnCustomFloat() {
				return -0.07f;
			}
		}, 0.1f);
		systems.add(sys1);
		return sys1;
	}

	// public static ParticleSystem createTreeFire(Tree tree) {
	// ParticleSystem sys2 = new ParticleSystem(fire, new Tool() {
	// @Override
	// public Vector3f returnCustomVect() {
	// return Vects.randomVector3f(tree.getPos(), tree.getTop());
	// }
	// }, new Tool() {
	// @Override
	// public Vector3f returnCustomVect() {
	// return Vects.randomVector3f(new Vector3f(-1.5f * tree.getScale(), 0,
	// -1.5f * tree.getScale()),
	// new Vector3f(1.5f * tree.getScale(), 0, 1.5f * tree.getScale()));
	// }
	// }, new Tool() {
	// @Override
	// public float returnCustomFloat() {
	// return Meth.randomFloat(1.5f, 3f * tree.getScale());
	// }
	// }, new Tool() {
	// @Override
	// public float returnCustomFloat() {
	// return Meth.randomFloat(1, 2);
	// }
	// }, new Tool() {
	// @Override
	// public float returnCustomFloat() {
	// return -0.05f;
	// }
	// }, 0.015f);
	// systems.add(sys2);
	// return sys2;
	// }

}
