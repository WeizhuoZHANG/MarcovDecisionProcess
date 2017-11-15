package solver;

public class MDArray {
    private int dimension;
    private double[][] array2D;
    private double[][][] array3D;

    MDArray(int dimension, int row){
        this.dimension = dimension;
        if (dimension == 2) {
            array2D = new double[row][row];
        } else if (dimension == 3){
            array3D = new double[row][row][row];
        }
    }

    public double getValue(int row, int column){
        return array2D[row][column];
    }

    public double getValue(int row, int column, int height){
        return array3D[row][column][height];
    }

    public void setValue(int row, int column, double value){
        array2D[row][column] = value;
    }

    public void setValue(int row, int column, int height, double value){
        array3D[row][column][height] = value;
    }
}
