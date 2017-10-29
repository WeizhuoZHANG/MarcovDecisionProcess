package problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Matrix {
	
	private int numRows;
	private int numCols;
	private ArrayList<ArrayList<Double>> data;
	
	public Matrix(double[][] input) {
		numRows = input.length;
		numCols = input[0].length;
		data = new ArrayList<ArrayList<Double>>(numRows);
		for (int i = 0; i < numRows; i++) {
			data.add(new ArrayList<Double>(numCols));
			for (int j = 0; j < numCols; j++) {
				data.get(i).add(input[i][j]);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++)
				stringBuilder.append(data.get(i).get(j));
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder.toString();
	}

	public double get(int row, int col) {
		return data.get(row).get(col);
	} 
	
	public List<Double> getRow(int row) {
		return Collections.unmodifiableList(data.get(row));
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}
}
