package implementations;

import interfaces.Parser;
import interfaces.Particle;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

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
            File staticFile = new File(getClass().getClassLoader().getResource("static.txt").getFile());
            Scanner staticReader = new Scanner(staticFile);

            File dynamicFile = new File(getClass().getClassLoader().getResource("dynamic.txt").getFile());
            Scanner dynamicReader = new Scanner(dynamicFile);

            N = staticReader.nextInt();
            L = staticReader.nextDouble();
            Rc = staticReader.nextDouble();

            dynamicReader.nextDouble(); //Skip time

            int i = 0;

            while(staticReader.hasNext() && dynamicReader.hasNext()){
                Particle p = new ParticleImpl(dynamicReader.nextDouble(),dynamicReader.nextDouble(),staticReader.nextDouble(),i);
                particles.add(p);
                i++;
            }

            double idealM = particles.get(0).getRadius()*2 + Rc;

            M = L/Math.floor(L/idealM);

//            for (Particle p : particles) {
//                System.out.println(p.getId());
//            }


        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


}
