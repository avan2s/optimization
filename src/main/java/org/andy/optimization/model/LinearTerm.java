package org.andy.optimization.model;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearTerm {
    private double coefficent;
    private DecisionVariable decisionVariable;

    public LinearTerm(double coefficent, DecisionVariable decisionVariable) {
        this.coefficent = coefficent;
        this.decisionVariable = decisionVariable;
    }

    public double getCoefficent() {
        return coefficent;
    }

    public void setCoefficent(double coefficent) {
        this.coefficent = coefficent;
    }

    public DecisionVariable getDecisionVariable() {
        return decisionVariable;
    }

    public void setDecisionVariable(DecisionVariable decisionVariable) {
        this.decisionVariable = decisionVariable;
    }
}
