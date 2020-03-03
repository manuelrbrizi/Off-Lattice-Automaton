import implementations.ParserImpl;
import interfaces.Cell;
import interfaces.Parser;

import implementations.GridImpl;
import interfaces.Grid;
import interfaces.Particle;

import java.util.List;

public class CellIndexMethod {
    public static void main(String[] args){

        Parser p = new ParserImpl();
        p.parse();

    }

    public void doMethod(int L, int M, int N, int Rc){
        Grid grid = new GridImpl(L, M);
        int xCellPosition = 0, yCellPosition = 0, cellQuantity = M * M;

        for(int i = 0; i < cellQuantity; i++){
            if(i == M-1){
                xCellPosition = 0;
                yCellPosition++;
            }
            else{

            }
        }
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

}
