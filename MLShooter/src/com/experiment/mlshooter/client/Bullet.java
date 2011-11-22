package com.experiment.mlshooter.client;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends MovingObject {

	private final List<Target> targets;

	public Bullet(float x, float y, double rotation, List<Target> targets) {
		super(x, y, 0, rotation, 700, 20);
		System.out.println("new bullet rotation " + rotation);
		this.targets = clone(targets);
	}
	
	private List<Target> clone(List<Target> targets2) {
		List<Target> res = new ArrayList<Target>();
		for (Target target : targets2) {
			res.add(target.clone());
		}
		return res;
	}

	public List<Target> getTargets() {
		return targets;
	}
}
