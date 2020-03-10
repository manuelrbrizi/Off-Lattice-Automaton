package interfaces;

import java.util.List;
import java.util.Set;

public interface Particle {

    double calculateDistance(Particle p);
    double calculatePeriodicDistance(Particle p,double L);
    double calculateDistance(double newX, double newY, double radius);
    double getX();
    double getY();
    double getRadius();
    int getId();
    Set<Particle> getNeighbours();
    void setNeighbours(Set<Particle> neighbours);

}
