package implementations;

import interfaces.Parser;
import interfaces.Particle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;


public class ParserImpl implements Parser {
    private List<Particle> particles;
    private double L;
    private double M;
    private int N;
    private double Rc;

    public ParserImpl(){
        this.particles = new ArrayList<Particle>();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public double getL() {
        return L;
    }

    public double getRc() {
        return Rc;
    }

    public double getM() {
        return M;
    }

    public int getN() {
        return N;
    }

    public void parse() {
        try {
//            File staticFile = new File(getClass().getClassLoader().getResource("static.txt").getFile());
        File staticFile = new File("static.txt");

        Scanner staticReader = new Scanner(staticFile);

//            File dynamicFile = new File(getClass().getClassLoader().getResource("dynamic.txt").getFile());
            File dynamicFile = new File("dynamic.txt");

            Scanner dynamicReader = new Scanner(dynamicFile);

            N = staticReader.nextInt();
            L = staticReader.nextDouble();
            Rc = staticReader.nextDouble();

            //Skip time
            dynamicReader.nextDouble();

            int i = 0;

            while(staticReader.hasNext() && dynamicReader.hasNext()){
                Particle p = new ParticleImpl(dynamicReader.nextDouble(),dynamicReader.nextDouble(),staticReader.nextDouble(),i);
                particles.add(p);
                i++;
            }

            double idealM = particles.get(0).getRadius()*2 + Rc;
            M = L/Math.floor(L/idealM);

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


}
