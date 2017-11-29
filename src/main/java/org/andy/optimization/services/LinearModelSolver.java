package org.andy.optimization.services;

import org.andy.optimization.enums.DecisionVariableType;
import org.andy.optimization.model.*;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.SimplexSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Andy on 05.06.2017.
 */
// TODO: do not take combinational decision variables into account
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
                    if (decisionVariable.getVariableType() == DecisionVariableType.COMBINATION) {
                        CombinationVariable combiVar = (CombinationVariable) decisionVariable;
                        // find the binary decision variable w1, w2, w3, that caintains value 1
                        DecisionVariable variableWithBinaryTrue = this.findVariableWithBinaryTrue(solution, combiVar);
                        // the coefficent of the  wi is the solution, extract it
                        Double coefficient = combiVar.findCoefficient(variableWithBinaryTrue);

                        problemSolution.getVariableToSolutionValue().put(decisionVariable, coefficient);
                    } else {
                        problemSolution.getVariableToSolutionValue().put(decisionVariable, solution.getPoint()[decisionVariable.getIndex()]);
                    }
                }
                problemSolution.setObjectiveValue(solution.getValue());
            } catch (NoFeasibleSolutionException ex) {
                return null;
            }
        }
        return problemSolution;
    }

    private DecisionVariable findVariableWithBinaryTrue(PointValuePair solution, CombinationVariable variable) {
        List<DecisionVariable> subVariables = variable.getSubVariables();
        for (DecisionVariable subVariable : subVariables) {
            double value = solution.getPoint()[subVariable.getIndex()];
            // don'tdo value == 1, because in the bnB there can be continious values
            if (value != 0) {
                return subVariable;
            }
        }
        throw new NoFeasibleSolutionException();
    }

    public ProblemSolution solve() {
        BranchAndBound branchAndBound = new BranchAndBound(this.optimizationModel);
        ProblemSolution problemSolution = branchAndBound.solve();
        // TODO: make solution result -> not found or something, but create also an instance of problem solutionResult
        if (problemSolution != null) {
            problemSolution.setTimeElapsed(branchAndBound.getElapsedTime());
        }
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
            // calculate all coefficients
            double[] coefficientsForConstraintLhs = this.createCoefficientsByLinearExpression(constraintToConvert.getLinearExpressionLhs());

            org.apache.commons.math3.optim.linear.LinearConstraint constraint;
            // if there is no term on the right hand side => just adjust the constant
            if (constraintToConvert.getLinearExpressionRhs().getTerms().size() == 0) {
                // convert 2x + 3y + 10 <= 15 to: 2x + 3y <= 5
                double inclusiveBoundForCalculation = constraintToConvert.getLinearExpressionRhs().getConstant() - constraintToConvert.getLinearExpressionLhs().getConstant();
                constraint = new org.apache.commons.math3.optim.linear.LinearConstraint(coefficientsForConstraintLhs, constraintToConvert.getRelationship(), inclusiveBoundForCalculation);
            } else {
                double[] coefficientsForConstraintRhs = this.createCoefficientsByLinearExpression(constraintToConvert.getLinearExpressionRhs());
                double lhsConstant = constraintToConvert.getLinearExpressionLhs().getConstant();
                double rhsConstant = constraintToConvert.getLinearExpressionRhs().getConstant();
                constraint = new org.apache.commons.math3.optim.linear.LinearConstraint(coefficientsForConstraintLhs, lhsConstant,
                        constraintToConvert.getRelationship(), coefficientsForConstraintRhs, rhsConstant);
            }

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
        List<DecisionVariable> decisionVariables = this.optimizationModel.getDecisionVariables().stream()
                .filter(var -> var.getVariableType() != DecisionVariableType.COMBINATION)
                .collect(Collectors.toList());

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
            if (decisionVariable.getVariableType() != DecisionVariableType.COMBINATION) {
                decisionVariable.setIndex(i);
                i++;
            } else {
                // TODO: fix me
            }
        }
    }

    public void setOptimizationModel(LinearOptimizationModel optimizationModel) {
        this.optimizationModel = optimizationModel;
    }

    public LinearOptimizationModel getOptimizationModel() {
        return optimizationModel;
    }
}
