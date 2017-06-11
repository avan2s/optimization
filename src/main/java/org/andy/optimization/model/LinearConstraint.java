package org.andy.optimization.model;

import org.apache.commons.math3.optim.linear.Relationship;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearConstraint {
    private LinearExpression linearExpression;
    private Relationship relationship;
    private double inclusiveBound;

    public LinearConstraint(Relationship relationship, double inclusiveBound) {
        this.linearExpression = new LinearExpression();
        this.relationship = relationship;
        this.inclusiveBound = inclusiveBound;
    }

    public LinearConstraint(LinearExpression linearExpression, Relationship relationship, double inclusiveBound) {
        this.linearExpression = linearExpression;
        this.relationship = relationship;
        this.inclusiveBound = inclusiveBound;
    }

    public LinearConstraint(LinearExpression leftLinearExpression, Relationship relationship, LinearExpression rightLinearExpression) {
        this.linearExpression = leftLinearExpression;
        this.relationship = relationship;
    }

    public void addTerm(double coefficient, DecisionVariable decisionVariable) {
        this.linearExpression.addTerm(coefficient, decisionVariable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.linearExpression.toString());
        sb.append(" ").append(this.relationship).append(" ").append(this.getInclusiveBound());
        return sb.toString();
    }

    public void addConstant(double value) {
        this.linearExpression.addConstant(value);
    }

    public void setConstant(double value){
        this.linearExpression.setConstant(value);
    }


    public LinearExpression getLinearExpression() {
        return linearExpression;
    }

    public void setLinearExpression(LinearExpression linearExpression) {
        this.linearExpression = linearExpression;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public double getInclusiveBound() {
        return inclusiveBound;
    }
}
