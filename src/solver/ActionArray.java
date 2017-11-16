package solver;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class ActionArray {
    private int dimension;
    private Point2D[][] array2D;
    private Point3D[][][] array3D;

    ActionArray(int dimension, int row){
        this.dimension = dimension;
        if (dimension == 2) {
            array2D = new Point2D[row][row];
        } else if (dimension == 3){
            array3D = new Point3D[row][row][row];
        }
    }

    public Point2D getValue(int row, int column){
        return array2D[row][column];
    }

    public Point3D getValue(int row, int column, int height){
        return array3D[row][column][height];
    }

    public void setValue(int row, int column, Point2D value){
        array2D[row][column] = value;
    }

    public void setValue(int row, int column, int height, Point3D value){
        array3D[row][column][height] = value;
    }
}
