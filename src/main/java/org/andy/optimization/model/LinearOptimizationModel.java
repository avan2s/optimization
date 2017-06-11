package org.andy.optimization.model;

import org.andy.optimization.api.ILinearOptimizationModel;
import org.apache.commons.math3.optim.linear.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearOptimizationModel implements ILinearOptimizationModel {


    private List<DecisionVariable> decisionVariables;
    private List<LinearConstraint> linearConstraints;
    private LinearObjective objective;


    public LinearOptimizationModel() {
        this.decisionVariables = new ArrayList<>();
        this.linearConstraints = new ArrayList<>();
    }

    @Override
    public void addDecisionVariable(DecisionVariable variable) {
        if (!this.decisionVariables.contains(variable)) {
            this.decisionVariables.add(variable);
            LinearExpression linearExpression = new LinearExpression();
            linearExpression.addTerm(1, variable);

            // variable >= lowerBound
            LinearConstraint lowerBoundConstraint = new LinearConstraint(linearExpression, Relationship.GEQ, variable.getLowerInclusiveBound());
            // variable <= upperBound
            LinearConstraint upperBoundConstraint = new LinearConstraint(linearExpression, Relationship.LEQ, variable.getUpperInclusiveBound());
            this.linearConstraints.add(lowerBoundConstraint);
            this.linearConstraints.add(upperBoundConstraint);
        }
    }

    @Override
    public void addLinearConstraint(LinearConstraint constraint) {
        List<LinearTerm> terms = constraint.getLinearExpressionLhs().getTerms();
        for (LinearTerm term : terms) {
            DecisionVariable decisionVariable = term.getDecisionVariable();
            this.addDecisionVariable(decisionVariable);
        }
        this.linearConstraints.add(constraint);
    }

    @Override
    public void setObjective(LinearObjective objective) {
    /* List<LinearTerm> currentObjectiveTerms = this.objective.getLinearExpressionLhs().getTerms();
        for(LinearTerm currentObjectiveTerm : currentObjectiveTerms){
            DecisionVariable decisionVariable = currentObjectiveTerm.getDecisionVariable();
            for(LinearConstraint linearConstraint : linearConstraints){
                List<LinearTerm> constraintTerms = linearConstraint.getLinearExpressionLhs().getTerms();
                for(LinearTerm constraintTerm : constraintTerms){
                    if(constraintTerm.getDecisionVariable())
                }
            }

            this.addDecisionVariable(decisionVariable);
        }*/

        // TODO: delete decision variables if not available in any constraint anymore
        List<LinearTerm> terms = objective.getLinearExpression().getTerms();
        for (LinearTerm term : terms) {
            DecisionVariable decisionVariable = term.getDecisionVariable();
            this.addDecisionVariable(decisionVariable);
        }
        this.objective = objective;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.objective).append("\ns.t.\n");
        for (LinearConstraint constraint : this.linearConstraints) {
            sb.append(constraint).append(",\n");
        }
        return sb.toString();
    }

    public List<DecisionVariable> getDecisionVariables() {
        return decisionVariables;
    }

    public List<LinearConstraint> getLinearConstraints() {
        return linearConstraints;
    }

    public LinearObjective getObjective() {
        return objective;
    }

    public void setDecisionVariables(List<DecisionVariable> decisionVariables) {
        this.decisionVariables = decisionVariables;
    }

    public void setLinearConstraints(List<LinearConstraint> linearConstraints) {
        this.linearConstraints = linearConstraints;
    }
}
