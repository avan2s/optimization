package org.andy.optimization.model;

import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearObjective {

    private GoalType goalType;
    private LinearExpression linearExpression;


    public LinearObjective(GoalType goalType) {
        this.goalType = goalType;
        this.linearExpression = new LinearExpression();
    }

    public GoalType getGoalType() {
        return this.goalType;
    }

    public void addTerm(double coefficient, DecisionVariable decisionVariable) {
        this.linearExpression.addTerm(coefficient, decisionVariable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.goalType).append(": ").append(this.linearExpression.toString());
        return sb.toString();
    }

    public void setConstant(double value) {

    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public LinearExpression getLinearExpression() {
        return linearExpression;
    }

    public void setLinearExpression(LinearExpression linearExpression) {
        this.linearExpression = linearExpression;
    }
}
