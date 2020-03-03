import implementations.CellImpl;
import implementations.ParserImpl;
import interfaces.Cell;
import interfaces.Parser;

import implementations.GridImpl;
import interfaces.Grid;
import interfaces.Particle;

import java.util.ArrayList;
import java.util.List;

public class CellIndexMethod {
    public static void main(String[] args){
        Parser p = new ParserImpl();
        p.parse();
    }

    public void fillGrid(Parser parser){
        double L = parser.getL();
        double M = parser.getM();
        int N = parser.getN();
        Grid grid = new GridImpl(L, M, 0.001);
        int xCellPosition = 0, yCellPosition = 0;
        int cellQuantity =  (int) (L/M) * (int)(L/M);
        List<Cell> cellList = new ArrayList<Cell>();
        Cell cell;

        for(int i = 0; i < cellQuantity; i++){
            if(i == M){
                xCellPosition = 0;
                yCellPosition++;
                cell = new CellImpl(xCellPosition, yCellPosition);
                cellList.add(cell);
            }
            else{
                cell = new CellImpl(xCellPosition, yCellPosition);
                cellList.add(cell);
                xCellPosition++;
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
    }

    private int calculateCellNumber(double x, double y, double M, double L){
        return (int) (Math.min(Math.floor(x/M), (L/M)-1) + (int)(L/M) * Math.min(Math.floor(y/M), (L/M)-1));
    }

    private void cellIndexMethod(Grid grid){
        for(Cell c : grid.getCells()){
            for(Particle p : c.getParticles()){
                getNeighbours(p,c.getX(),c.getY(),grid);
                getNeighbours(p,c.getX(),c.getY()+1,grid);
                getNeighbours(p,c.getX()+1,c.getY()+1,grid);
                getNeighbours(p,c.getX()+1,c.getY(),grid);
                getNeighbours(p,c.getX()+1,c.getY()-1,grid);
            }
        }
    }

    private void getNeighbours(Particle p, double x, double y, Grid g){
        int cellsPerRow = g.getL()/g.getM();
        Cell c = g.getCells().get((int) (x+y*cellsPerRow));

        for(Particle other : c.getParticles()){
            if(p.calculateDistance(other)<0.5){
                p.getNeighbours().add(other);
                other.getNeighbours().add(p);
            }
        }

    }

    private void testGridCreation(Grid grid){
        Cell c;
        Particle p;

        for(int i = 0; i < grid.getCells().size(); i++){
            c = grid.getCells().get(i);
            for(int j = 0; j < c.getParticles().size(); j++){
                p = c.getParticles().get(j);
                System.out.println(String.format("(%f, %f) ID = %d - CELL = %d\n", p.getX(), p.getY(), p.getId(), i));
            }
        }
    }
}
