package org.andy.optimization.services;

import org.andy.optimization.enums.DecisionVariableType;
import org.andy.optimization.model.*;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.*;

/**
 * Created by Andy on 05.06.2017.
 */
public class BranchAndBound {

    private LinearModelSolver modelSolver;
    private LinearOptimizationModel rootOptimizationModel;
    private Map.Entry<LinearOptimizationModel, ProblemSolution> currentBest = null;
    private long nodesGenerated = 0;
    private double elapsedTime = 0;
    private GoalType goalType;
    private Queue<LinearOptimizationModel> activeProblems;

    public BranchAndBound(LinearOptimizationModel model) {
        this.modelSolver = new LinearModelSolver(model);
        this.activeProblems = new LinkedList<>();
        this.rootOptimizationModel = model;
        this.activeProblems.add(rootOptimizationModel);
        this.goalType = this.rootOptimizationModel.getObjective().getGoalType();
    }

    public ProblemSolution solve() {
        LinearOptimizationModel selectedProblem;
        Date d0 = new Date();

        while (!this.activeProblems.isEmpty()) {
            // select a problem and remove it from the queue
            selectedProblem = this.activeProblems.poll();
            System.out.println(selectedProblem);
            this.modelSolver.setOptimizationModel(selectedProblem);
            ProblemSolution solution = this.modelSolver.solveContiniously();

            // TODO: and there is a solution found in the problem
            if (solution == null) {
                continue;
            }

            // BranchingCriteria:
            Map.Entry<DecisionVariable, Double> nextIntegerVariable = this.nextIntegerVariable(solution);

            // (1) if we have currently no solution
            // (2) if we have a valid solution found (with integers), that is better, than the currentBestSolution -> replace the solution
            boolean isValidSolution = nextIntegerVariable == null;
            if ((isValidSolution && this.currentBest == null) || (isValidSolution && solution.isBetterThan(this.currentBest.getValue(), this.goalType))) {
                this.currentBest = new MyEntry<>(selectedProblem, solution);
            }
            // do branching
            else {
                // if we have a valid current best solution already and the current solution (also without integers) is not better - than there is no hope, that there will be a better value on branching
                // so don't branch
                if (this.currentBest != null && !solution.isBetterThan(this.currentBest.getValue(), this.goalType)) {
                    continue;
                }

                DecisionVariable decisionVariable = nextIntegerVariable.getKey();
                Double solutionValue = nextIntegerVariable.getValue();


                // create relaxations - do branching
                List<LinearOptimizationModel> relaxations = this.createRelaxations(selectedProblem, decisionVariable, solutionValue);
                for (LinearOptimizationModel relaxation : relaxations) {
                    this.activeProblems.add(relaxation);
                    this.nodesGenerated++;
                }
            }
        }

        // calculate the calculation time
        Date d1 = new Date();
        this.elapsedTime = (double) (d1.getTime() - d0.getTime()) / 1000;

        if (this.currentBest == null) {
            return null;
        }

        return this.currentBest.getValue();
    }

    private Map.Entry<DecisionVariable, Double> nextIntegerVariable(ProblemSolution problemSolution) {
        Map<DecisionVariable, Double> variableToSolutionValue = problemSolution.getVariableToSolutionValue();
        for (Map.Entry<DecisionVariable, Double> entry : variableToSolutionValue.entrySet()) {
            DecisionVariable decisionVariable = entry.getKey();
            Double solutionValue = entry.getValue();
            if (decisionVariable.getVariableType() == DecisionVariableType.INTEGER || decisionVariable.getVariableType() == DecisionVariableType.BINARY) {
                double doubleValue = solutionValue.doubleValue();
                // if is integer
                if ((!(doubleValue == Math.floor(doubleValue)) && !Double.isInfinite(doubleValue))) {
                    return entry;
                }
            }
        }
        return null;
    }


    private LinearOptimizationModel createRelaxation(LinearOptimizationModel optimizationModel, DecisionVariable decisionVariable, double solutionValue, Relationship relationship) {
        LinearOptimizationModel relaxation = new LinearOptimizationModel();

        // put all decision variable References inside from the current problem
        List<DecisionVariable> decisionVariables = new ArrayList<>(optimizationModel.getDecisionVariables());
        List<LinearConstraint> linearConstraints = new ArrayList<>(optimizationModel.getLinearConstraints());
        relaxation.setDecisionVariables(decisionVariables);
        relaxation.setLinearConstraints(linearConstraints);
        relaxation.setObjective(optimizationModel.getObjective());

        // add relaxation Constraint
        double roundedValue = 0;

        // round up: decisionVariable >= upRoundedValue
        if (relationship == Relationship.GEQ) {
            roundedValue = Math.ceil(solutionValue);
        }
        // decisionVariable <= downRoundedValue
        else {
            roundedValue = Math.floor(solutionValue);
        }

        // decisionVariable >= upRoundedValue
        LinearConstraint linearConstraint = new LinearConstraint(relationship, roundedValue);
        linearConstraint.addTerm(1, decisionVariable);
        relaxation.addLinearConstraint(linearConstraint);
        return relaxation;
    }

    private List<LinearOptimizationModel> createRelaxations(LinearOptimizationModel optimizationModel, DecisionVariable decisionVariable, double solutionValue) {
        List<Relationship> relationships = Arrays.asList(Relationship.LEQ, Relationship.GEQ);
        List<LinearOptimizationModel> relaxations = new ArrayList<>();
        relationships.forEach(relationship -> relaxations.add(this.createRelaxation(optimizationModel, decisionVariable, solutionValue, relationship)));
        return relaxations;
    }

    public double getElapsedTime() {
        return this.elapsedTime;
    }

    public long getNodeCount() {
        return this.nodesGenerated;
    }
}
