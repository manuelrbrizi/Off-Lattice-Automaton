package implementations;

import interfaces.Cell;
import interfaces.Grid;

import java.util.LinkedList;
import java.util.List;

public class GridImpl implements Grid {
    private int L;
    private int M;

    private List<Cell> cells;

    public GridImpl(int L, int M){
        this.L = L;
        this.M = M;
        this.cells = new LinkedList<Cell>();
    }

    public int getL() {
        return L;
    }

    public int getM() {
        return M;
    }

    public List<Cell> getCells() {
        return cells;
    }
}
