package implementations;

import interfaces.Particle;

import java.util.List;

public class ParticleImpl implements Particle {
    private double x;
    private double y;
    private double radius;
    private int id;
    private List<Particle> neighbours;

    public ParticleImpl(double x, double y, double radius, int id) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
    }

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

    public List<Particle> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Particle> neighbours) {
        this.neighbours = neighbours;
    }
}
