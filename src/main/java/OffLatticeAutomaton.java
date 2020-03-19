import implementations.CellImpl;
import implementations.ParserImpl;
import interfaces.Cell;
import interfaces.Parser;

import implementations.GridImpl;
import interfaces.Grid;
import interfaces.Particle;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OffLatticeAutomaton {
    public static void main(String[] args){

        generateInputFile(300,5,1,0.1,0.03);

        Parser p = new ParserImpl();
        p.parse();
        Grid grid = fillGrid(p);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("outputOVITO.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();

        //For cantidad de intervalos de tiempo
        int TIME = 500;
        int prevCellNumber, newCellNumber;
        double random;

        for(int i = 0; i < TIME; i++) {
            //Sacamos los vecinos previos
            for(Particle particle : grid.getParticles()){
                particle.getNeighbours().clear();
            }

            //Calculamos vecinos
            CIM(grid);

            //Cambiamos valor de NewAngle
            for (Particle particle : grid.getParticles()) {
                random = Math.random()*(p.getNu())-(p.getNu()/2);
                particle.setNewAngle(calculateNewAngle(particle, random));
            }

            //Cambiamos valor de posicion y switcheamos ponemos NewAngle en Angle (OJO con condiciones periodicas)
            for(Particle particle : grid.getParticles()){
                prevCellNumber = calculateCellNumber(particle.getX(), particle.getY(), grid.getM(), grid.getL());
                particle.calculateNewPosition(1, grid.getL());
                newCellNumber = calculateCellNumber(particle.getX(), particle.getY(), grid.getM(), grid.getL());

                if(prevCellNumber != newCellNumber){
                    grid.getCells().get(prevCellNumber).getParticles().remove(particle);
                    grid.getCells().get(newCellNumber).getParticles().add(particle);
                }

                particle.setAngle(particle.getNewAngle());
            }

        // testGridCreation(grid);
        generateOvitoFile(grid);
        //generateNeighboursFile(grid);
        }
    }

    private static double calculateNewAngle(Particle p, double nu){
        double cos = Math.cos(p.getAngle());
        double sin = Math.sin(p.getAngle());

        for(Particle other : p.getNeighbours()){
            cos += Math.cos(other.getAngle());
            sin += Math.sin(other.getAngle());
        }

        cos = cos/(p.getNeighbours().size()+1);
        sin = sin/(p.getNeighbours().size()+1);

        return Math.atan2(sin, cos) + nu;
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


    private static void CIM(Grid grid){
        for(Cell c : grid.getCells()){
            for(Particle p : c.getParticles()){
                getNeighbours2(p,c.getX(),c.getY(),grid);
                getNeighbours2(p,c.getX(),c.getY()+1,grid);
                getNeighbours2(p,c.getX()+1,c.getY()+1,grid);
                getNeighbours2(p,c.getX()+1,c.getY(),grid);
                getNeighbours2(p,c.getX()+1,c.getY()-1,grid);
            }
        }
    }

    private static void getNeighbours2(Particle p, int x, int y, Grid grid) {
        int cellsPerRow = (int) (grid.getL()/grid.getM());

        if(y==cellsPerRow){
            y = 0;
        }
        else if(y==-1){
            y += cellsPerRow;
        }

        if(x == cellsPerRow){
            x = 0;
        }

        Cell c = grid.getCells().get((int)(x+y*cellsPerRow));

        for(Particle other : c.getParticles()){
            if(p.getId() != other.getId() && p.calculatePeriodicDistance(other,grid.getL()) < grid.getRc()){
                p.getNeighbours().add(other);
                other.getNeighbours().add(p);
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
            if(cellsPerRow == 1){
                searchForNeighbours(p, g.getCells().get(cellsPerRow*(cellsPerRow-1)).getParticles(), 0, -g.getL(), g);
            }
            else{
                searchForNeighbours(p, g.getCells().get(cellsPerRow*(cellsPerRow-1)+1).getParticles(), 0, -g.getL(), g);
            }
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
            searchForNeighbours(p, g.getCells().get(0).getParticles(), 0, g.getL(), g);
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
//        else if(x == 0.0){
//            searchForNeighbours(p, g.getCells().get(cellNumber+(cellsPerRow-1)).getParticles(), -g.getL(), 0, g);
//            searchForNeighbours(p, g.getCells().get(cellNumber+(cellsPerRow-1)+cellsPerRow).getParticles(), -g.getL(), 0, g);
//            searchForNeighbours(p, g.getCells().get(cellNumber+(cellsPerRow-1)-cellsPerRow).getParticles(), -g.getL(), 0, g);
//        }

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
            if(p.calculateDistance(other.getX()+newX, other.getY()+newY) < g.getRc()){
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

    //PREVIOUS METHODS. WILL BE DELETED SOON


    private static void generateOvitoFile(Grid grid){


        StringBuilder sb = new StringBuilder();
        sb.append(grid.getParticles().size());
        sb.append("\n");
        sb.append("\n");

        for (Particle p: grid.getParticles()){
            sb.append(p.getX());
            sb.append("\t");
            sb.append(p.getY());
            sb.append("\t");
            sb.append(p.getXVelocity());
            sb.append("\t");
            sb.append(p.getYVelocity());
            sb.append("\t");
            sb.append(0.03);
            sb.append("\t");

            Color color = Color.getHSBColor((float) p.getNewAngle(),1F,1F);
            //sb.append(color.getRed());
            double red = Math.abs(((p.getNewAngle())%(2*Math.PI))/(Math.PI*2));
            double blue = 1 - red;
            double green = 0.5 * red;
            sb.append(red);
            sb.append("\t");
            //sb.append(color.getGreen());
            sb.append(blue);
            sb.append("\t");
            //sb.append(color.getBlue());
            //sb.append(green);
            sb.append("\n");
        }

        try {

            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter("outputOVITO.txt", true));
            out.write(sb.toString());
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
//        try {
//            FileWriter myWriter = new FileWriter("outputOVITO.txt");
//            myWriter.write(sb.toString());
//            myWriter.close();
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }

    }
/*
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
     */

    private static void generateInputFile(int quantity, double L, double Rc, double Nu, double vel){
        StringBuilder sb = new StringBuilder();
        sb.append(quantity);
        sb.append("\n");
        sb.append(L);
        sb.append("\n");
        sb.append(Rc);
        sb.append("\n");
        sb.append(Nu);
        sb.append("\n");
        Random rand = new Random();
        for (int i = 0; i < quantity; i++){
            sb.append(rand.nextDouble()*L);
            sb.append(" ");
            sb.append(rand.nextDouble()*L);
            sb.append(" ");
            sb.append(vel);
            sb.append(" ");
            sb.append(rand.nextDouble()* Math.PI*2);
            sb.append("\n");
        }

        try {
            FileWriter myWriter = new FileWriter("input.txt");
            myWriter.write(sb.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }

}
