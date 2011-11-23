package com.experiment.mlshooter.client;

public class Target extends MovingObject {

	public Target(float x, float y, double rotation) {
		super(x, y, 10, rotation, 100 + (float) (100 + Math.random() * 200), 20);
	}
	
	private Target(long id, float x, float y, float speed, double rotation) {
		super(id, x, y, 10, rotation, speed, 20);
	}
	
	public Target clone() {
		return new Target(this.getId(), this.x, this.y, this.speed, this.rotation);
	}
}
