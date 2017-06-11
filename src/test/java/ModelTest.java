import org.andy.optimization.enums.DecisionVariableType;
import org.andy.optimization.model.*;
import org.andy.optimization.services.LinearModelSolver;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 05.06.2017.
 */

public class ModelTest {

    @Test
    public void testRounds() {
        double ceil = Math.ceil(-3.6D);
        double floor = Math.floor(-3.6D);
        System.out.println(">= " + ceil);
        System.out.println("<= " + floor);
    }

    @Test
    public void testModel() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{30, 40}, 0);


        List<LinearConstraint> constraints = new ArrayList();

        // x + y <= 240
        constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.LEQ, 240));
        // 2x + y <= 320
        constraints.add(new LinearConstraint(new double[]{2, 1}, Relationship.LEQ, 320));
        // x,y >=0
        NonNegativeConstraint nonNegativeConstraint = new NonNegativeConstraint(true);


        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);
        SimplexSolver linearOptimizer = new SimplexSolver();
        // put everything together in order to get a maximization problem
        // now i receive org.apache.commons.math3.optim.linear.UnboundedSolutionException: unbounded solution
        PointValuePair solution = linearOptimizer.optimize(f, constraintSet, GoalType.MAXIMIZE, nonNegativeConstraint);


        if (solution != null) {
            //get solution
            double max = solution.getValue();
            double x = solution.getPoint()[0];
            double y = solution.getPoint()[1];
            System.out.println("x = " + x);
            System.out.println("y = " + y);
            System.out.println("Opt: " + max);
        }
        Assert.assertEquals(true, true);
    }

    @Test
    public void testModelWithOwnApi() {
        // Define decision variables
        DecisionVariable dX = new DecisionVariable("x", DecisionVariableType.CONTINIOUS, 0);
        DecisionVariable dY = new DecisionVariable("y", DecisionVariableType.CONTINIOUS, 5);
        DecisionVariable dZ = new DecisionVariable("z", DecisionVariableType.CONTINIOUS, 0);

        // Define objective function
        LinearObjective linearObjective = new LinearObjective(GoalType.MAXIMIZE);
        linearObjective.addTerm(10, dX);
        linearObjective.addTerm(20, dY);
        linearObjective.addTerm(40, dZ);

        // Define constraints
        org.andy.optimization.model.LinearConstraint linearConstraint1 = new org.andy.optimization.model.LinearConstraint(Relationship.LEQ, 48.2);
        linearConstraint1.addTermLhs(1, dX);
        linearConstraint1.addTermLhs(1, dY);
        System.out.println(linearConstraint1);

        org.andy.optimization.model.LinearConstraint linearConstraint2 = new org.andy.optimization.model.LinearConstraint(Relationship.LEQ, 200);
        linearConstraint2.addTermLhs(2, dZ);
        linearConstraint2.addTermLhs(2, dY);

        org.andy.optimization.model.LinearConstraint linearConstraint3 = new org.andy.optimization.model.LinearConstraint(Relationship.LEQ, 90);
        linearConstraint3.addTermLhs(1, dY);
        linearConstraint3.addTermLhs(1, dZ);


        // construct the model
        LinearOptimizationModel model = new LinearOptimizationModel();
        model.addDecisionVariable(dX);
        model.addDecisionVariable(dY);
        model.addLinearConstraint(linearConstraint1);
        model.addLinearConstraint(linearConstraint2);
        model.addLinearConstraint(linearConstraint3);
        model.setObjective(linearObjective);

        System.out.println(model);
        // Solve the problem with a solver
        LinearModelSolver linearModelSolver = new LinearModelSolver(model);
        ProblemSolution solution = linearModelSolver.solve();
        for (DecisionVariable decisionVariable : model.getDecisionVariables()) {
            System.out.println(decisionVariable.getName() + " = " + solution.getVariableToSolutionValue().get(decisionVariable));
        }
        System.out.println("objective = " + solution.getObjectiveValue());
    }

    @Test
    public void testModelWithOwnApiMIP() {
        // Define decision variables
        DecisionVariable dX = new DecisionVariable("x", DecisionVariableType.INTEGER);
        DecisionVariable dY = new DecisionVariable("y", DecisionVariableType.INTEGER, 0);

        // Define objective function
        LinearObjective linearObjective = new LinearObjective(GoalType.MAXIMIZE);
        linearObjective.addTerm(1, dX);
        linearObjective.addTerm(2, dY);

        // Define constraints
        org.andy.optimization.model.LinearConstraint linearConstraint1 = new org.andy.optimization.model.LinearConstraint(Relationship.LEQ, 7);
        linearConstraint1.addTermLhs(1, dX);
        linearConstraint1.addTermLhs(3, dY);

        org.andy.optimization.model.LinearConstraint linearConstraint2 = new org.andy.optimization.model.LinearConstraint(Relationship.LEQ, 10);
        linearConstraint2.addTermLhs(3, dX);
        linearConstraint2.addTermLhs(2, dY);


        // construct the model
        LinearOptimizationModel model = new LinearOptimizationModel();
        model.addDecisionVariable(dX);
        model.addDecisionVariable(dY);
        model.addLinearConstraint(linearConstraint1);
        model.addLinearConstraint(linearConstraint2);
        model.setObjective(linearObjective);

        // Solve the problem with a solver
        LinearModelSolver linearModelSolver = new LinearModelSolver(model);
        ProblemSolution solution = linearModelSolver.solve();
        for (DecisionVariable decisionVariable : model.getDecisionVariables()) {
            System.out.println(decisionVariable.getName() + " = " + solution.getVariableToSolutionValue().get(decisionVariable));
        }
        System.out.println("objective = " + solution.getObjectiveValue());
        System.out.println("elapsedTime:" + solution.getTimeElapsed() + " ms");
    }


}
