package interfaces;

import java.util.List;
import java.util.Set;

public interface Particle {

    double calculateDistance(Particle p);
    double getX();
    double getY();
    double getRadius();
    int getId();
    Set<Particle> getNeighbours();
    void setNeighbours(Set<Particle> neighbours);
}
