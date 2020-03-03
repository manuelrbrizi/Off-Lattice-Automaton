package implementations;

import interfaces.Particle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleImpl implements Particle {
    private double x;
    private double y;
    private double radius;
    private int id;
    private Set<Particle> neighbours;

    public ParticleImpl(double x, double y, double radius, int id) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
        this.neighbours = new HashSet<Particle>();
    }

    public ParticleImpl(){}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public int getId() {
        return id;
    }

    public Set<Particle> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<Particle> neighbours) {
        this.neighbours = neighbours;
    }

    /* Now considering the border of the particle */
    public double calculateDistance(Particle p) {
        double toReturn = Math.sqrt(Math.pow(p.getX()-getX(),2) + Math.pow(p.getY()-getY(),2)) - p.getRadius() - getRadius();
        return toReturn < 0 ? 0 : toReturn;
    }
}
