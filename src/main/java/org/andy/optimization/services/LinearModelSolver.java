package org.andy.optimization.services;

import org.andy.optimization.model.*;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.SimplexSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearModelSolver {

    private LinearOptimizationModel optimizationModel;
    private SimplexSolver linearOptimizer = new SimplexSolver();
    private BranchAndBound branchAndBound;

    public LinearModelSolver(LinearOptimizationModel optimizationModel) {
        this.optimizationModel = optimizationModel;
    }

    public ProblemSolution solve() {
        ProblemSolution problemSolution = null;
        this.setUpIndices();

        LinearConstraintSet constraintSet = this.createLinearConstraintSet(); // new LinearConstraintSet(this.linearConstraints);
        LinearObjectiveFunction linearObjectiveFunction = this.createLinearObjectiveFunction();
        if (linearObjectiveFunction != null) {
            PointValuePair solution = linearOptimizer.optimize(linearObjectiveFunction, constraintSet, this.optimizationModel.getObjective().getGoalType());
            problemSolution = new ProblemSolution();
            for (DecisionVariable decisionVariable : this.optimizationModel.getDecisionVariables()) {
                problemSolution.getVariableToSolutionValue().put(decisionVariable,solution.getPoint()[decisionVariable.getIndex()]);
            }
            problemSolution.setObjectiveValue(solution.getValue());
        }
        return problemSolution;
    }

    private LinearObjectiveFunction createLinearObjectiveFunction() {
        LinearObjective objective = this.optimizationModel.getObjective();
        LinearExpression linearExpression = objective.getLinearExpression();
        double[] coefficients = this.createCoefficientsByLinearExpression(linearExpression);
        LinearObjectiveFunction f = new LinearObjectiveFunction(coefficients, 0);
        return f;
    }

    private LinearConstraintSet createLinearConstraintSet() {
        List<org.andy.optimization.model.LinearConstraint> constraintsToConvert = this.optimizationModel.getLinearConstraints();

        // constraints for apache commons math 3
        List<LinearConstraint> constraints = new ArrayList<>();

        // prepare the constraints for apache commons math 3 by own defined constraints inside the model
        for (org.andy.optimization.model.LinearConstraint constraintToConvert : constraintsToConvert) {
            double[] coefficientsForConstraint = this.createCoefficientsByLinearExpression(constraintToConvert.getLinearExpression());
            LinearConstraint constraint = new LinearConstraint(coefficientsForConstraint, constraintToConvert.getRelationship(), constraintToConvert.getInclusiveBound());
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
