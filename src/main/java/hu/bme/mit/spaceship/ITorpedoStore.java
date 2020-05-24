package hu.bme.mit.spaceship;

public interface ITorpedoStore {
	public boolean fire(final int numberOfTorpedos);
	public boolean isEmpty();
	public int getTorpedoCount();
}