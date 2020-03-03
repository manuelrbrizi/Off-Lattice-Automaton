package interfaces;

import java.util.List;

public interface Parser {
    void parse();
    double getL();
    double getM();
    int getN();
    List<Particle> getParticles();
}
