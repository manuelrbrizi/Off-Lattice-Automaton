import implementations.GridImpl;
import interfaces.Grid;
import interfaces.Particle;

import java.util.List;

public class CellIndexMethod {
    public static void main(String[] args){

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
}
