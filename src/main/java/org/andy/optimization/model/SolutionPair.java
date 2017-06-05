package org.andy.optimization.model;

/**
 * Created by Andy on 05.06.2017.
 */
public class SolutionPair {
    private DecisionVariable decisionVariable;
    private double solution;

    public SolutionPair(DecisionVariable decisionVariable, double solution) {
        this.decisionVariable = decisionVariable;
        this.solution = solution;
    }

    public DecisionVariable getDecisionVariable() {
        return decisionVariable;
    }

    public void setDecisionVariable(DecisionVariable decisionVariable) {
        this.decisionVariable = decisionVariable;
    }

    public double getSolution() {
        return solution;
    }

    public void setSolution(double solution) {
        this.solution = solution;
    }
}
