package com.experiment.mlshooter.client;

public class MovingObject {

	private static long obj_count = 0;
	
	private long id;
	public float x;
	public float y;
	public float mass;
	public double rotation;
	public float speed;
	public float collisionRadius;
	public boolean removed;
	
	public MovingObject(float x, float y, float mass, double rotation, float speed, float collisionRadius) {
		id = obj_count++; 
		this.x = x;
		this.y = y;
		this.mass = mass;
		this.rotation = rotation;
		this.speed = speed;
		this.collisionRadius = collisionRadius;
	}
	
	protected MovingObject(long id, float x, float y, float mass, double rotation, float speed, float collisionRadius) {
		this.id = id; 
		this.x = x;
		this.y = y;
		this.mass = mass;
		this.rotation = rotation;
		this.speed = speed;
		this.collisionRadius = collisionRadius;
	}
	
	public void remove() {
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MovingObject)) {
			return false;
		}
		MovingObject mobj = (MovingObject) obj;
		return this == obj || mobj.getId() == this.getId();
	}
}
