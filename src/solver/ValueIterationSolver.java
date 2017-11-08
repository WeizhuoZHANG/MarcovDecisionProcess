package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import problem.Matrix;
import problem.ProblemSpec;
import problem.VentureManager;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ValueIterationSolver implements FundingAllocationAgent {

	private ProblemSpec spec = new ProblemSpec();
	private VentureManager ventureManager;
    private List<Matrix> probabilities;
    private List<Matrix> transations;
    private List<double[]> rewards;

	public ValueIterationSolver(ProblemSpec spec) throws IOException {
		this.spec = spec;
		ventureManager = spec.getVentureManager();
		probabilities = spec.getProbabilities();
		transations = new ArrayList<>();
		rewards = new ArrayList<>();
		for (int i = 0; i < Math.pow(ventureManager.getMaxManufacturingFunds() + 1, ventureManager.getNumVentures()); i++){

		}
	}
	
	public void doOfflineComputation() {
	    // TODO Write your own code here.
		for (int i = 0; i < probabilities.size(); i++){
			Matrix probability = probabilities.get(i);
			double price = spec.getSalePrices().get(i);
			transations.add(computeTransation(probability));
			rewards.add(computeReward(probability, price));
		}
	}

	public Matrix computeTransation(Matrix probability){
		int numRows = probability.getNumRows();
		int numCols = probability.getNumCols();
		double[][] transation = new double[numRows][numCols];
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numCols; j++){
				if (j == 0){
					for (int k = i; k < numRows; k++)
						transation[i][j] += probability.get(i,k);
				} else if (j > 0 && j <= i) {
					transation[i][j] = probability.get(i, i - j);
				} else if (j > i){
					transation[i][j] = 0;
				}
			}
		}
		return new Matrix(transation);
	}

	public static double[] computeReward(Matrix probability, double price){
		int numRows = probability.getNumRows();
		int numCols = probability.getNumCols();
		double[] reward = new double[numRows];
		//this is given by Tutorial 8
//		for (int x = 0; x < numRows; x++){
//			for (int y = x + 1; y < numCols; y++)
//				reward[x] += probability.get(x, y) * (x - y);
//		}

		for (int x = 0; x < numRows; x++){
			for (int y = 0; y <= x; y++){
				reward[x] += probability.get(x, y) * price * 0.6 * y;
			}
			for (int y = x + 1; y < numRows; y++){
				reward[x] += probability.get(x, y) * price * (0.6 * x - 0.25 * (y - x));
			}
		}

		for (int i = 0; i < numRows; i++){
			System.out.println(reward[i]);
		}

		return reward;
	}

	public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
														  int numFortnightsLeft) {
		// Example code that allocates an additional $10 000 to each venture.
		// TODO Replace this with your own code.

		List<Integer> additionalFunding = new ArrayList<Integer>();

		int totalManufacturingFunds = 0;
		for (int i : manufacturingFunds) {
			totalManufacturingFunds += i;
		}
		
		int totalAdditional = 0;
		for (int i = 0; i < ventureManager.getNumVentures(); i++) {
			if (totalManufacturingFunds >= ventureManager.getMaxManufacturingFunds() ||
			        totalAdditional >= ventureManager.getMaxAdditionalFunding()) {
				additionalFunding.add(0);
			} else {
				additionalFunding.add(1);
				totalAdditional ++;
				totalManufacturingFunds ++;
			}
		}

		return additionalFunding;
	}

}
