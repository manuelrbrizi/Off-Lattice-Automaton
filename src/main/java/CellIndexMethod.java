import implementations.CellImpl;
import implementations.ParserImpl;
import implementations.ParticleImpl;
import interfaces.Cell;
import interfaces.Parser;

import implementations.GridImpl;
import interfaces.Grid;
import interfaces.Particle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CellIndexMethod {
    public static void main(String[] args){
//        System.out.println("N\t\t\tCMI\t\t\tBF\t\t\t\n");
//
//        for(int i = 50;i<=5000;i+=50){
//            for(int j = 0;j<5;j++){
//                System.out.printf("%d\t\t\t",i);
//                generateFiles(i,20,0.25,1);
//                Parser p = new ParserImpl();
//                p.parse();
//                Grid grid = fillGrid(p);
//
//                long start = System.nanoTime();
//                cellIndexMethod(grid);
//                //bruteForce(grid);
//                long end = System.nanoTime();
//                long elapsedTime = end - start;
//                System.out.printf("%d\t\t\t",elapsedTime);
//
//                start = System.nanoTime();
//                //cellIndexMethod(grid);
//                bruteForce(grid,true);
//                end = System.nanoTime();
//                elapsedTime = end - start;
//                System.out.printf("%d\t\t\t\n",elapsedTime);
//
//            }
//        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese 1 si quiere generar un nuevo set de archivos o 2 para continuar");
        int files = scanner.nextInt();
        if(files == 1){
            double L,R,Rc;
            int N;
            System.out.println("Ingrese el valor de L");
            L = scanner.nextDouble();
            System.out.println("Ingrese el valor de R");
            R = scanner.nextDouble();
            System.out.println("Ingrese el valor de Rc");
            Rc = scanner.nextDouble();
            System.out.println("Ingrese el valor de N");
            N = scanner.nextInt();
            generateFiles(N,L,R,Rc);
        }

        System.out.println("Ingrese 1 para CIM o 2 para BF");
        boolean cim = scanner.nextInt() == 1;

        System.out.println("Ingrese 1 para no periodico o 2 para periodico");
        boolean periodic = scanner.nextInt() == 2;

        System.out.println("Ingrese que valor de M quiere usar o 0 para usar el calculado");
        double userm = scanner.nextDouble();



        Parser p = new ParserImpl();
        p.parse();
        if(userm > 0){

            p.setM(p.getL()/Math.floor(p.getL()/userm));
            System.out.printf("El M se ha setteado en %f\n",p.getM());
        }
        else {
            System.out.printf("El M calculado es %f\n",p.getM());
        }
        
        Grid grid = fillGrid(p);



        long start = System.nanoTime();
        if(cim){
            cellIndexMethod(grid,periodic);
        }
        else{
            bruteForce(grid,periodic);
        }
        long end = System.nanoTime();
        long elapsedTime = end - start;
        System.out.printf("Elapsed time: %dms\n",elapsedTime/1000000);

        System.out.println("Ingrese el ID de la particula distinguida");
        int selected = scanner.nextInt();



       // testGridCreation(grid);

        generateOvitoFile(grid,selected);
        generateNeighboursFile(grid);

    }



    private static Grid fillGrid(Parser parser){
        double L = parser.getL();
        double M = parser.getM();
        double Rc = parser.getRc();
        int N = parser.getN();
        Grid grid = new GridImpl(L, M, Rc);
        int xCellPosition = 0, yCellPosition = 0;
        int cellQuantity =  (int) (L/M) * (int)(L/M);
        List<Cell> cellList = new ArrayList<Cell>();
        Cell cell = new CellImpl(0, 0);
        cellList.add(cell);

        for(int i = 1; i < cellQuantity; i++){
            int cellsPerRow = (int) (L/M);
            if(i % cellsPerRow == 0){
                xCellPosition = 0;
                yCellPosition++;
                cell = new CellImpl(xCellPosition, yCellPosition);
                cellList.add(cell);
                //System.out.println(String.format("Putting X = %d, Y = %d, I = %d\n", xCellPosition, yCellPosition, i));
            }
            else{
                xCellPosition++;
                cell = new CellImpl(xCellPosition, yCellPosition);
                cellList.add(cell);
            }
        }

        List<Particle> particles = parser.getParticles();
        Particle p;
        int cellNumber;

        for(int i = 0; i < N; i++){
            p = particles.get(i);
            cellNumber = calculateCellNumber(p.getX(), p.getY(), M, L);
            cellList.get(cellNumber).addParticle(p);
        }

        grid.setCells(cellList);
        return grid;
    }

    private static int calculateCellNumber(double x, double y, double M, double L){
        return (int) (Math.min(Math.floor(x/M), (L/M)-1) + (int)(L/M) * Math.min(Math.floor(y/M), (L/M)-1));
    }

    private static void cellIndexMethod(Grid grid, boolean periodic){
        for(Cell c : grid.getCells()){
            for(Particle p : c.getParticles()){
                if(periodic){
                    getPeriodicNeighbours(p,c.getX(),c.getY(),grid);
                }
                getNeighbours(p,c.getX(),c.getY(),grid);
                getNeighbours(p,c.getX(),c.getY()+1,grid);
                getNeighbours(p,c.getX()+1,c.getY()+1,grid);
                getNeighbours(p,c.getX()+1,c.getY(),grid);
                getNeighbours(p,c.getX()+1,c.getY()-1,grid);
            }
        }
    }

    private static void getNeighbours(Particle p, double x, double y, Grid g){
        int cellsPerRow = (int) (g.getL()/g.getM());
        if(x < 0 || x >= cellsPerRow || y < 0 || y >= cellsPerRow){
            return;
        }

        else{
            Cell c = g.getCells().get((int)(x+y*cellsPerRow));


            for(Particle other : c.getParticles()){
                if(p.getId() != other.getId() && p.calculateDistance(other) < g.getRc()){
                    p.getNeighbours().add(other);
                    other.getNeighbours().add(p);
                }
            }
        }
    }

    private static void getPeriodicNeighbours(Particle p, double x, double y, Grid g){
        int cellsPerRow = (int) (g.getL()/g.getM());
        int cellNumber = (int) (x+y*cellsPerRow);

        /* periodicity in the bottom left */
        if(x == 0.0 && y == 0.0){
            //searchForNeighbours(p, g.getCells().get(cellsPerRow*(cellsPerRow-1)).getParticles(), 0, -g.getL(), g);
            //searchForNeighbours(p, g.getCells().get(cellsPerRow*cellsPerRow-1).getParticles(), -g.getL(), -g.getL(), g);
            //searchForNeighbours(p, g.getCells().get(cellsPerRow-1).getParticles(), -g.getL(), 0, g);
            searchForNeighbours(p, g.getCells().get(cellsPerRow*(cellsPerRow-1)+1).getParticles(), 0, -g.getL(), g);
        }

        /* periodicity in the bottom right */
        else if(x == cellsPerRow - 1 && y == 0.0){
            searchForNeighbours(p, g.getCells().get(cellsPerRow*(cellsPerRow-1)).getParticles(), g.getL(), -g.getL(), g);
            //searchForNeighbours(p, g.getCells().get(cellsPerRow*cellsPerRow-1).getParticles(), 0, -g.getL(), g);
            searchForNeighbours(p, g.getCells().get(0).getParticles(), g.getL(), 0, g);
            searchForNeighbours(p, g.getCells().get(cellsPerRow).getParticles(), g.getL(), 0, g);
        }

        /* periodicity in the top left */
        else if(x == 0.0 && y == cellsPerRow - 1){
            //searchForNeighbours(p, g.getCells().get(cellsPerRow*cellsPerRow-1).getParticles(), -g.getL(), 0, g);
            //searchForNeighbours(p, g.getCells().get(cellsPerRow-1).getParticles(), -g.getL(), g.getL(), g);
            searchForNeighbours(p, g.getCells().get(0).getParticles(), 0, -g.getL(), g);
            searchForNeighbours(p, g.getCells().get(0).getParticles(), cellNumber-(cellsPerRow*(cellsPerRow-1)+1), -g.getL(), g);
        }

        /* periodicity in the top right */
        else if(x == cellsPerRow - 1 && y == cellsPerRow - 1){
            searchForNeighbours(p, g.getCells().get(cellsPerRow*(cellsPerRow-1)).getParticles(), g.getL(), 0, g);
            searchForNeighbours(p, g.getCells().get(cellsPerRow-1).getParticles(), 0, g.getL(), g);
            searchForNeighbours(p, g.getCells().get(0).getParticles(), g.getL(), g.getL(), g);
            searchForNeighbours(p, g.getCells().get(cellsPerRow).getParticles(), g.getL(), 0, g);
        }

        /* periodicity in the left */
        //else if(x == 0.0){
            //searchForNeighbours(p, g.getCells().get(cellNumber+(cellsPerRow-1)).getParticles(), -g.getL(), 0, g);
            //searchForNeighbours(p, g.getCells().get(cellNumber+(cellsPerRow-1)+cellsPerRow).getParticles(), -g.getL(), 0, g);
            //searchForNeighbours(p, g.getCells().get(cellNumber+(cellsPerRow-1)-cellsPerRow).getParticles(), -g.getL(), 0, g);
        //}

        /* periodicity in the right */
        else if(x == cellsPerRow-1){
            searchForNeighbours(p, g.getCells().get(cellNumber-(cellsPerRow-1)).getParticles(), g.getL(), 0, g);
            searchForNeighbours(p, g.getCells().get(cellNumber-(cellsPerRow-1)+cellsPerRow).getParticles(), g.getL(), 0, g);
            searchForNeighbours(p, g.getCells().get(cellNumber-(cellsPerRow-1)-cellsPerRow).getParticles(), g.getL(), 0, g);
        }

        /* periodicity in the bottom */
        else if(y == 0.0){
            //searchForNeighbours(p, g.getCells().get(cellNumber+cellsPerRow*(cellsPerRow-1)).getParticles(), 0, -g.getL(), g);
            //searchForNeighbours(p, g.getCells().get(cellNumber+cellsPerRow*(cellsPerRow-1)-1).getParticles(), 0, -g.getL(), g);
            searchForNeighbours(p, g.getCells().get(cellNumber+cellsPerRow*(cellsPerRow-1)+1).getParticles(), 0, -g.getL(), g);
        }

        /* periodicity in the top */
        else if(y == cellsPerRow-1){
            searchForNeighbours(p, g.getCells().get(cellNumber-cellsPerRow*(cellsPerRow-1)).getParticles(), 0, g.getL(), g);
            //searchForNeighbours(p, g.getCells().get(cellNumber-cellsPerRow*(cellsPerRow-1)-1).getParticles(), 0, g.getL(), g);
            searchForNeighbours(p, g.getCells().get(cellNumber-cellsPerRow*(cellsPerRow-1)+1).getParticles(), 0, g.getL(), g);
        }
    }

    private static void searchForNeighbours(Particle p, List<Particle> particleList, double newX, double newY, Grid g){
        for(Particle other : particleList){
            if(p.calculateDistance(other.getX()+newX, other.getY()+newY, other.getRadius()) < g.getRc()){
                p.getNeighbours().add(other);
                other.getNeighbours().add(p);
            }
        }
    }

    private static void testGridCreation(Grid grid){
        Cell c;
        Particle p;

        for(int i = 0; i < grid.getCells().size(); i++){
            c = grid.getCells().get(i);
            for(int j = 0; j < c.getParticles().size(); j++){
                p = c.getParticles().get(j);
                System.out.println(String.format("(%f, %f) ID = %d - CELL = %d, NEIGH = %d\n", p.getX(), p.getY(), p.getId(), i, p.getNeighbours().size()));
            }
        }
    }

    private static void generateOvitoFile(Grid grid, int selectedParticle){
        List<Particle> particles = new ArrayList<Particle>();

        for(Cell c : grid.getCells()){
            particles.addAll(c.getParticles());
        }

        Particle chosen = new ParticleImpl();
        for(Particle p : particles) {
            if (p.getId() == selectedParticle) {
                chosen = p;
            }
        }
        int red,green,blue;

        StringBuilder sb = new StringBuilder();
        sb.append(particles.size());
        sb.append("\n\n");

        for(Particle p : particles){
            if(p.getId() == selectedParticle){
                red = 0;
                green = 255;
                blue = 0;
            }
            else if(chosen.getNeighbours().contains(p)){
                red = 255;
                green = 0;
                blue = 0;            }
            else{
                red = 0;
                green = 0;
                blue = 255;            }
            sb.append(String.format("%f\t%f\t%f\t%d\t%d\t%d\n",p.getX(),p.getY(),p.getRadius(),red,green,blue));

        }
        sb.deleteCharAt(sb.lastIndexOf("\n"));

        try {
            FileWriter myWriter = new FileWriter("outputOVITO.txt");
            myWriter.write(sb.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private static void generateNeighboursFile(Grid grid) {

        List<Particle> particles = new ArrayList<Particle>();

        for(Cell c : grid.getCells()){
            particles.addAll(c.getParticles());
        }


        StringBuilder sb = new StringBuilder();

        for(Particle p : particles){
            sb.append(String.format("%d\t->\t",p.getId()));
            for (Particle n : p.getNeighbours()){
                sb.append(n.getId());
                sb.append("\t");
            }
            sb.append("\n");
        }

        try {
            FileWriter myWriter = new FileWriter("outputNeighbours.txt");
            myWriter.write(sb.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


    private static void bruteForce(Grid grid, boolean hasPeriodicity){
        List<Particle> particles = new ArrayList<Particle>();

        for(Cell c : grid.getCells()){
            particles.addAll(c.getParticles());
        }



        for(Particle p : particles){
            for(Particle other : particles){
                if(hasPeriodicity){
                    getPeriodicNeighbours(p, Math.floor(p.getX()/ grid.getM()), Math.floor(p.getY()/ grid.getM()), grid);
                }

                if(p.getId() != other.getId() && p.calculateDistance(other)<grid.getRc() && !p.getNeighbours().contains(other)){
                    p.getNeighbours().add(other);
                    other.getNeighbours().add(p);
                }
            }
        }
    }


    private static void generateFiles(int quantity, double L, double r, double Rc){
        createStaticFile(quantity,L,r,Rc);
        createDynamicFile(quantity,L);
    }

    private static void createStaticFile(int quantity, double L, double r, double Rc){
        StringBuilder sb = new StringBuilder();
        sb.append(quantity);
        sb.append("\n");
        sb.append(L);
        sb.append("\n");
        sb.append(Rc);
        sb.append("\n");
        for (int i = 0; i < quantity; i++){
            sb.append(r);
            sb.append("\n");
        }

        try {
//            File staticFile = new File(CellIndexMethod.class.getClassLoader().getResource("static.txt").getFile());
//            FileWriter myWriter = new FileWriter(staticFile);

            FileWriter myWriter = new FileWriter("static.txt");
            myWriter.write(sb.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private static void createDynamicFile(int quantity, double max){
        StringBuilder sb = new StringBuilder();
        sb.append("10\n");
        Random r = new Random();

        for (int i = 0; i < quantity; i++){
            sb.append(r.nextDouble()*max);
            sb.append(" ");
            sb.append(r.nextDouble()*max);
            sb.append("\n");
        }

        try {

//            File dynamicFile = new File(CellIndexMethod.class.getClassLoader().getResource("dynamic.txt").getFile());
//            FileWriter myWriter = new FileWriter(dynamicFile);
            FileWriter myWriter = new FileWriter("dynamic.txt");
            myWriter.write(sb.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
