package com.experiment.mlshooter.client;

public class Collision {

	public MovingObject a;
	public MovingObject b;
	
	public Collision(MovingObject a, MovingObject b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean equals(Object obj) {
		Collision other = (Collision) obj;
		return other.a == this.a && other.b == this.b || other.a == this.b && other.b == this.a;
	}
	
}
