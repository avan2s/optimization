package org.andy.optimization.services;

import org.andy.optimization.model.*;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.SimplexSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearModelSolver {

    private LinearOptimizationModel optimizationModel;
    private SimplexSolver linearOptimizer = new SimplexSolver();

    public LinearModelSolver(LinearOptimizationModel optimizationModel) {
        this.optimizationModel = optimizationModel;
    }

    public ProblemSolution solveContiniously() {
        ProblemSolution problemSolution = null;
        this.setUpIndices();

        LinearConstraintSet constraintSet = this.createLinearConstraintSet(); // new LinearConstraintSet(this.linearConstraints);
        LinearObjectiveFunction linearObjectiveFunction = this.createLinearObjectiveFunction();
        if (linearObjectiveFunction != null) {
            try {
                PointValuePair solution = linearOptimizer.optimize(linearObjectiveFunction, constraintSet, this.optimizationModel.getObjective().getGoalType());
                problemSolution = new ProblemSolution();
                for (DecisionVariable decisionVariable : this.optimizationModel.getDecisionVariables()) {
                    problemSolution.getVariableToSolutionValue().put(decisionVariable, solution.getPoint()[decisionVariable.getIndex()]);
                }
                problemSolution.setObjectiveValue(solution.getValue());
            } catch (NoFeasibleSolutionException ex) {
                return null;
            }
        }
        return problemSolution;
    }

    public ProblemSolution solve() {
        BranchAndBound branchAndBound = new BranchAndBound(this.optimizationModel);
        ProblemSolution problemSolution = branchAndBound.solve();
        problemSolution.setTimeElapsed(branchAndBound.getElapsedTime());
        return problemSolution;
    }

    private LinearObjectiveFunction createLinearObjectiveFunction() {
        LinearObjective objective = this.optimizationModel.getObjective();
        LinearExpression linearExpression = objective.getLinearExpression();
        double[] coefficients = this.createCoefficientsByLinearExpression(linearExpression);
        LinearObjectiveFunction f = new LinearObjectiveFunction(coefficients, linearExpression.getConstant());
        return f;
    }

    private LinearConstraintSet createLinearConstraintSet() {
        List<LinearConstraint> constraintsToConvert = this.optimizationModel.getLinearConstraints();

        // constraints for apache commons math 3
        List<org.apache.commons.math3.optim.linear.LinearConstraint> constraints = new ArrayList<>();

        // prepare the constraints for apache commons math 3 by own defined constraints inside the model
        for (LinearConstraint constraintToConvert : constraintsToConvert) {
            // convert 2x + 3y + 10 <= 15 to: 2x + 3y <= 5
            double inclusiveBoundForCalculation = constraintToConvert.getInclusiveBound() - constraintToConvert.getLinearExpression().getConstant();
            // calculate all coefficients
            double[] coefficientsForConstraint = this.createCoefficientsByLinearExpression(constraintToConvert.getLinearExpression());
            org.apache.commons.math3.optim.linear.LinearConstraint constraint = new org.apache.commons.math3.optim.linear.LinearConstraint
                    (coefficientsForConstraint, constraintToConvert.getRelationship(), inclusiveBoundForCalculation);
            constraints.add(constraint);
        }

        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);
        return constraintSet;
    }

    private double[] createCoefficientsByLinearExpression(LinearExpression expression) {
        List<LinearTerm> terms = expression.getTerms();
        double[] coefficients = this.createInitialCoefficientsForEachDecisionVariable();
        for (LinearTerm term : terms) {
            int variableIndex = term.getDecisionVariable().getIndex();
            coefficients[variableIndex] = term.getCoefficent();
        }
        return coefficients;
    }

    private double[] createInitialCoefficientsForEachDecisionVariable() {
        List<DecisionVariable> decisionVariables = this.optimizationModel.getDecisionVariables();
        double[] coefficents = new double[decisionVariables.size()];
        for (int i = 0; i < coefficents.length; i++) {
            coefficents[i] = 0;
        }
        return coefficents;
    }

    private void setUpIndices() {
        List<DecisionVariable> decisionVariables = this.optimizationModel.getDecisionVariables();
        // each variable gets an index
        int i = 0;
        for (DecisionVariable decisionVariable : decisionVariables) {
            decisionVariable.setIndex(i);
            i++;
        }
    }

    public void setOptimizationModel(LinearOptimizationModel optimizationModel) {
        this.optimizationModel = optimizationModel;
    }

    public LinearOptimizationModel getOptimizationModel() {
        return optimizationModel;
    }
}
