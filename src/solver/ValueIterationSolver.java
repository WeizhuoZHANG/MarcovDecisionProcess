package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import problem.Matrix;
import problem.ProblemSpec;
import problem.VentureManager;

import javax.print.attribute.standard.NumberUp;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ValueIterationSolver implements FundingAllocationAgent {

	private ProblemSpec spec = new ProblemSpec();
	private VentureManager ventureManager;
	private int maxFund;
	private int maxAddition;
	private double discount;
    private List<Matrix> probabilities;
    private List<Matrix> transations;
//    private List<List<Double>> rewards;
    private int numberOfVentures;
    private MDArray reward;
    private MDArray value;
    private ActionArray actionArray;

	public ValueIterationSolver(ProblemSpec spec) throws IOException {
		this.spec = spec;
		ventureManager = spec.getVentureManager();
		maxFund = ventureManager.getMaxManufacturingFunds();
		maxAddition = ventureManager.getMaxAdditionalFunding();
		discount = spec.getDiscountFactor();
		probabilities = spec.getProbabilities();
		transations = new ArrayList<>();
		numberOfVentures = ventureManager.getNumVentures();
		reward = new MDArray(numberOfVentures, maxFund + 1);
		value = new MDArray(numberOfVentures, maxFund + 1);
		actionArray = new ActionArray(numberOfVentures, maxFund + 1);
//		actionArray = new ActionArray(numberOfVentures, maxAddition + 1);
	}
	
	public void doOfflineComputation() {
	    // TODO Write your own code here.
		List<List<Double>> r = new ArrayList<>();
		for (int i = 0; i < probabilities.size(); i++){
			Matrix probability = probabilities.get(i);
			double price = spec.getSalePrices().get(i);
			transations.add(computeTransation(probability));
			r.add(computeReward(probability, price));
		}
		initValueFunction(numberOfVentures, r);
		doValueIteration();
		printAction();
	}

	public void initValueFunction(int dimension, List<List<Double>> r){
		if (dimension == 2){
			for (int i = 0; i <= maxFund; i++) {
				for (int j = 0; j <= maxFund; j++){
//					double r = rewardFunction(i, j);
//					value.setValue(i, j, r);
					reward.setValue(i, j, rewardFunction(i, j, r));
					value.setValue(i, j, reward.getValue(i, j));
				}
			}
		} else if (dimension == 3) {
			for (int i = 0; i <= maxFund; i++) {
				for (int j = 0; j <= maxFund; j++) {
					for (int k = 0; k <= maxFund; k++) {
//						double r = rewardFunction(i, j, k);
//						value.setValue(i, j, k, r);
						reward.setValue(i, j, k, rewardFunction(i, j, k, r));
						value.setValue(i, j, k, reward.getValue(i, j, k));
					}
				}
			}
		}
	}

	public void doValueIteration(){
		int iteration = 0;
		if (numberOfVentures == 2) {
			while (true) {
				int validValue = 0;
				iteration++;
				for (int i = 0; i <= maxFund; i++) {
					for (int j = 0; j <= (maxFund - i); j++) {
						double v = value.getValue(i, j);
						double maxValue = -Double.MAX_VALUE;
//						int action = Math.min(maxAddition, maxFund - i);
						int actionOne = Math.min(maxAddition, maxFund - i);
//						int actionTwo = Math.min(maxAddition, maxFund - j);
						for (int a = 0; a <= actionOne; a++) {
							int actionTwo = Math.min(maxAddition - a, maxFund - j);
							for (int b = 0; b <= actionTwo; b++) {
//						for (int a = 0; a <= actionOne; a++) {
//							for (int b = 0; b <= actionTwo; b++) {
								if ((i + j + a + b) > maxFund){
									break;
								}
								double sumOfTransation = 0;
								for (int orderOne = 0; orderOne <= i + a; orderOne++){
									for (int orderTwo = 0; orderTwo <= j + b; orderTwo++){
										double t1 = transations.get(0).get(i + a, orderOne);
										double t2 = transations.get(1).get(j + b, orderTwo);
										double vprime = value.getValue(orderOne, orderTwo);
										sumOfTransation += t1 * t2 * vprime;
									}
								}
								double tempValue = reward.getValue(i + a, j + b) + discount * sumOfTransation;

//								double tempValue = reward.getValue(i + a, j + b) + discount * sumOfTransation;
								if (tempValue > maxValue){
									maxValue = tempValue;
									actionArray.setValue(i, j, new Point2D(a, b));
								}
//								maxValue = Math.max(tempValue, maxValue);
							}
						}
						value.setValue(i, j, maxValue);
						if (Math.abs(v - maxValue) < Math.pow(10, -7)) {
							validValue++;
//						} else if (Math.abs(v - maxValue) < Math.pow(10, -6)){
//							System.out.println(i + " - " + j + ": " + (v - maxValue));
						}
					}
				}
				if (validValue == ((2 + maxFund) * (maxFund + 1) / 2)){
					break;
				}
//				System.out.println(validValue);
//				System.out.println(Math.pow(maxFund + 1, 2) - validValue);
			}
		} else if (numberOfVentures == 3){
			while (true) {
				int validValue = 0;
				iteration++;
				for (int i = 0; i <= maxFund; i++) {
					for (int j = 0; j <= (maxFund - i); j++) {
						for (int k = 0; k <= (maxFund - i - j); k++) {
							double v = value.getValue(i, j, k);
							double maxValue = -Double.MAX_VALUE;
							int actionOne = Math.min(maxAddition, maxFund - i);
							for (int a = 0; a <= actionOne; a++) {
								int actionTwo = Math.min(maxAddition - a, maxFund - j);
								for (int b = 0; b <= actionTwo; b++) {
									int actionThree = Math.min(maxAddition - b - a, maxFund - k);
									for (int c = 0; c <= actionThree; c++) {
										if ((i + j + k + a + b + c) > maxFund){
											break;
										}
										double sumOfTransation = 0;
										for (int orderOne = 0; orderOne <= i; orderOne++){
											for (int orderTwo = 0; orderTwo <= j; orderTwo++){
												for (int orderThree = 0; orderThree <= k; orderThree++)
												sumOfTransation += transations.get(0).get(i + a, orderOne) * transations.get(1).get(j + b, orderTwo) * transations.get(2).get(k + c, orderThree) * value.getValue(orderOne, orderTwo, orderThree);
											}
										}
										double tempValue = reward.getValue(i + a, j + b, k + c) + discount * sumOfTransation;
										if (tempValue > maxValue){
											maxValue = tempValue;
											actionArray.setValue(i, j, k, new Point3D(a, b, c));
										}
//										maxValue = Math.max(tempValue, maxValue);
									}
								}
							}
							value.setValue(i, j, k, maxValue);
							if (Math.abs(v - maxValue) < Math.pow(10, -7)) {
								validValue++;
//							} else if (Math.abs(v - maxValue) < Math.pow(10, -6)){
//								System.out.println(i + " - " + j + " - " + k + ": " + (v - maxValue));
							}
						}
					}
				}
				if (validValue == ((maxFund + 1) * (maxFund + 2) * (maxFund + 3) / 6)){
					break;
				}
//				System.out.println(validValue);
			}
		}
//		System.out.println("Iteration: " + iteration);
	}

	public void printAction(){
		if (numberOfVentures == 2){
			for (int i = 0; i <= maxFund; i++){
				for (int j = 0; j <= (maxFund - i); j++){
					System.out.println(i + ", " + j + " -> " + actionArray.getValue(i, j).toString());
				}
			}
		} else if (numberOfVentures == 3){
			for (int i = 0; i <= maxFund; i++){
				for (int j = 0; j <= (maxFund - i); j++){
					for (int k = 0; k <= (maxFund - i - j); k++) {
						System.out.println(i + ", " + j + ", " + k + " -> " + actionArray.getValue(i, j, k).toString());
					}
				}
			}
		}
	}

	public double rewardFunction(int i, int j, List<List<Double>> r){
		return r.get(0).get(i) + r.get(1).get(j);
	}

	public double rewardFunction(int i, int j, int k, List<List<Double>> r){
		return r.get(0).get(i) + r.get(1).get(j) + r.get(2).get(k);
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

	public List<Double> computeReward(Matrix probability, double price){
		int numRows = probability.getNumRows();
		int numCols = probability.getNumCols();
		List<Double> r = new ArrayList<>();
		for (int i = 0; i < numRows; i++){
			r.add(0.0);
		}
		//this is given by Tutorial 8
//		for (int x = 0; x < numRows; x++){
//			for (int y = x + 1; y < numCols; y++)
//				reward[x] += probability.get(x, y) * (x - y);
//		}

		for (int x = 0; x < numRows; x++){
			for (int y = 0; y <= x; y++){
				r.set(x, r.get(x) + probability.get(x, y) * price * 0.6 * y);
//				reward[x] += probability.get(x, y) * price * 0.6 * y;
			}
			for (int y = x + 1; y < numRows; y++){
				r.set(x, r.get(x) + probability.get(x, y) * price * (0.6 * x - 0.25 * (y - x)));
//				reward[x] += probability.get(x, y) * price * (0.6 * x - 0.25 * (y - x));
			}
		}

//		for (int i = 0; i < numRows; i++){
//			System.out.println(reward[i]);
//		}

		return r;
	}

	public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
														  int numFortnightsLeft) {
		// Example code that allocates an additional $10 000 to each venture.
		// TODO Replace this with your own code.

		List<Integer> additionalFunding = new ArrayList<Integer>();

		if (manufacturingFunds.size() == 2){
			int i = manufacturingFunds.get(0);
			int j = manufacturingFunds.get(1);
			Point2D point2D = actionArray.getValue(i, j);
			additionalFunding.add((int) point2D.getX());
			additionalFunding.add((int) point2D.getY());

		} else if (manufacturingFunds.size() == 3){
			int i = manufacturingFunds.get(0);
			int j = manufacturingFunds.get(1);
			int k = manufacturingFunds.get(2);
			Point3D point3D = actionArray.getValue(i, j, k);
			additionalFunding.add((int) point3D.getX());
			additionalFunding.add((int) point3D.getY());
			additionalFunding.add((int) point3D.getZ());
		}

		return additionalFunding;
	}

}
