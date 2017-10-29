package test.solver;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import problem.Matrix;
import solver.ValueIterationSolver;

/**
 * ValueIterationSolver Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Oct 27, 2017</pre>
 */
public class ValueIterationSolverTest {
    /**
     * Method: computeTransation(Matrix probability)
     */
    @Test
    public void testComputeTransation() throws Exception {
        //TODO: Test goes here...
        double[][] data = {
                {0.3, 0.2, 0.2, 0.1, 0.2},
                {0.3, 0.2, 0.2, 0.1, 0.2},
                {0.3, 0.2, 0.2, 0.1, 0.2},
                {0.3, 0.2, 0.2, 0.1, 0.2},
                {0.3, 0.2, 0.2, 0.1, 0.2}};
//        System.out.println(ValueIterationSolver.computeTransation(new Matrix(data)).toString());
    }

    /**
     * Method: computeReward(Matrix probability)
     */
    @Test
    public void testComputeReward() throws Exception {
        //TODO: Test goes here...
//        double[][] data = {
//                {0.3, 0.2, 0.2, 0.1, 0.2},
//                {0.3, 0.2, 0.2, 0.1, 0.2},
//                {0.3, 0.2, 0.2, 0.1, 0.2},
//                {0.3, 0.2, 0.2, 0.1, 0.2},
//                {0.3, 0.2, 0.2, 0.1, 0.2}};
        double[][] data = {
                {0.1, 0.5, 0.2, 0.2},
                {0.1, 0.6, 0.2, 0.1},
                {0.2, 0.3, 0.3, 0.2},
                {0.2, 0.3, 0.2, 0.3}};
        double price = 2.0;
        ValueIterationSolver.computeReward(new Matrix(data), price);
    }

} 
