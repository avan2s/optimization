package org.andy.optimization.model;

import org.apache.commons.math3.optim.linear.Relationship;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearConstraint {
    private LinearExpression linearExpressionLhs = new LinearExpression();
    private Relationship relationship;
    private LinearExpression linearExpressionRhs = new LinearExpression();

    public LinearConstraint(Relationship relationship) {
        this.relationship = relationship;
    }

    public LinearConstraint(Relationship relationship, double inclusiveBound) {
        this.relationship = relationship;
        this.linearExpressionRhs.setConstant(inclusiveBound);
    }

    public LinearConstraint(LinearExpression linearExpressionLhs, Relationship relationship, double inclusiveBound) {
        this.linearExpressionLhs = linearExpressionLhs;
        this.relationship = relationship;
        this.linearExpressionRhs.setConstant(inclusiveBound);
    }

    public LinearConstraint(LinearExpression linearExpressionLhs, Relationship relationship, LinearExpression linearExpressionRhs) {
        this.linearExpressionLhs = linearExpressionLhs;
        this.relationship = relationship;
        this.linearExpressionRhs = linearExpressionRhs;
    }

    public void addTermLhs(double coefficient, DecisionVariable decisionVariable) {
        this.linearExpressionLhs.addTerm(coefficient, decisionVariable);
    }

    public void addTermRhs(double coefficient, DecisionVariable decisionVariable) {
        this.linearExpressionRhs.addTerm(coefficient, decisionVariable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.linearExpressionLhs.toString());
        sb.append(" ").append(this.relationship).append(" ").append(this.linearExpressionRhs);
        return sb.toString();
    }

    public void addConstant(double value) {
        this.linearExpressionLhs.addConstant(value);
    }

    public void setConstantLhs(double value) {
        this.linearExpressionLhs.setConstant(value);
    }

    public void setConstantRhs(double value) {
        this.linearExpressionRhs.setConstant(value);
    }

    public LinearExpression getLinearExpressionLhs() {
        return linearExpressionLhs;
    }

    public LinearExpression getLinearExpressionRhs() {
        return linearExpressionRhs;
    }

    public void setLinearExpressionLhs(LinearExpression linearExpressionLhs) {
        this.linearExpressionLhs = linearExpressionLhs;
    }

    public void setLinearExpressionRhs(LinearExpression linearExpressionRhs) {
        this.linearExpressionRhs = linearExpressionRhs;
    }

    public void setBound(double value) {
        this.linearExpressionRhs.setConstant(value);
    }

    public Relationship getRelationship() {
        return relationship;
    }
}
