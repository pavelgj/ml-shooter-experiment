package com.experiment.mlshooter.client;

import java.util.ArrayList;
import java.util.List;

public class ShooterEngine {

	private static final float G = 9.1f;
	
	private List<MovingObject> objects = new ArrayList<MovingObject>();
	private List<MovingObject> toRemove = new ArrayList<MovingObject>();
	
	public void addObject(MovingObject obj) {
		objects.add(obj);
	}
	
	public void removeObject(MovingObject obj) {
		toRemove.add(obj);
	}
	
	public List<MovingObject> getObjects() {
		return objects;
	}
	
	public List<Collision> step(long milis) {
		for (MovingObject obj : new ArrayList<MovingObject>(toRemove)) {
			objects.remove(obj);
			toRemove.remove(obj);
		}
		List<Collision> collisions = new ArrayList<Collision>();
		int substeps = 5;
		long subtime = milis / substeps;
		for (int i = 0; i < substeps; i++) {
			subStep(subtime, collisions);
		}
		
		return collisions;
	}

	private void subStep(long milis, List<Collision> collisions) {
		float timeFactor = milis / 1000f;
		for (MovingObject obj : objects) {
			float speedX = (float) (Math.cos(obj.rotation) * obj.speed);
			float speedY = (float) (Math.sin(obj.rotation) * obj.speed /*+ obj.mass * G * timeFactor*/);
			
			double acos = Math.acos(speedX / Math.sqrt(speedX * speedX + speedY * speedY));
			double asin = Math.asin(speedY / Math.sqrt(speedX * speedX + speedY * speedY));
			
			if (asin >= 0) {
				obj.rotation = acos;
			} else {
				if (acos <= Math.PI / 2) {
					obj.rotation = 2 * Math.PI - acos;
				} else {
					obj.rotation = Math.PI - acos;
				}
			}
			
			obj.x += Math.cos(obj.rotation) * obj.speed * timeFactor; 
			obj.y += Math.sin(obj.rotation) * obj.speed * timeFactor;
		}
		
		for (MovingObject a : objects) {
			for (MovingObject b : objects) {
				if (a == b) {
					continue;
				}
				if (Math.abs(a.x - b.x) < (a.collisionRadius + b.collisionRadius) &&
						Math.abs(a.y - b.y) < (a.collisionRadius + b.collisionRadius)) {
					Collision coll = new Collision(a, b);
					if (!collisions.contains(coll)) {
						collisions.add(coll);
					}
				}
			}
		}
	}
}
