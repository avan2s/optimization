package org.andy.optimization.model;

import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andy on 05.06.2017.
 */
public class ProblemSolution {

    private Map<DecisionVariable, Double> variableToSolutionValue;
    private double objectiveValue;
    private double timeElapsed;

    public ProblemSolution() {
        this.variableToSolutionValue = new HashMap<>();
    }

    public boolean isBetterThan(ProblemSolution otherSolution, GoalType goalType) {
        if (otherSolution == null) {
            return false;
        }

        if (goalType == GoalType.MAXIMIZE) {
            if (this.objectiveValue > otherSolution.getObjectiveValue()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (this.objectiveValue < otherSolution.getObjectiveValue()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Map<DecisionVariable, Double> getVariableToSolutionValue() {
        return variableToSolutionValue;
    }

    public Double getSolutionValue(DecisionVariable decisionVariable) {
        return this.variableToSolutionValue.get(decisionVariable);
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public void setObjectiveValue(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
