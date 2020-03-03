package implementations;

import interfaces.Parser;
import interfaces.Particle;

import java.util.ArrayList;
import java.util.List;

public class ParserImpl implements Parser {
    private List<Particle> particles;
    private int L;
    private int M;

    public ParserImpl(){
        this.particles = new ArrayList<Particle>();
    }

    public void parse() {

    }
}
