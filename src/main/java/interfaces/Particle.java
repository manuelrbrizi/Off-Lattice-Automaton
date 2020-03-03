package interfaces;

import java.util.List;

public interface Particle {

    double calculateDistance(Particle p);
    double getX();
    double getY();
    double getRadius();
    int getId();
    List<Particle> getNeighbours();
    void setNeighbours(List<Particle> neighbours);
}
