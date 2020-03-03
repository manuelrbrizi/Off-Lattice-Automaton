package interfaces;

import java.util.List;

public interface Grid {
    void setCells(List<Cell> cellList);
    double getRc();
    int getL();
    int getM();
    List<Cell> getCells();
}
