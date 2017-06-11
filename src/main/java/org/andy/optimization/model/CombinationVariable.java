package org.andy.optimization.model;

import org.andy.optimization.enums.DecisionVariableType;
import org.apache.commons.math3.optim.linear.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy on 11.06.2017.
 */
public class CombinationVariable extends DecisionVariable {

    private Map<DecisionVariable, Double> variablesToCoefficent = new HashMap<>();
    private List<DecisionVariable> subVariables = new ArrayList<>();
    private LinearExpression linearExpressionToInsert = new LinearExpression();
    private LinearConstraint chooseOnlyOneConstraint;

    public CombinationVariable(String name, List<Double> allowedValues) {
        super(name, DecisionVariableType.COMBINATION);

        // for each allowed value one binary decision variable
        int nameIndex = 0;

        // allowed values [2,3,5] 0> insert binary sub decision variables where x1 means 2 is chosen, x2 => 3 is chosen, x3=> 5 is chosen, when xi=1 => chosen else xi=0 => not chosen
        // w1 + w2 + w3 = 1 => only one value can be chosen
        this.chooseOnlyOneConstraint = new LinearConstraint(Relationship.EQ, 1);

        // replace combiVar Xi in {2,3,5} with 2w1 + 3w2 + 5w3
        this.linearExpressionToInsert = new LinearExpression();
        for (Double allowedValue : allowedValues) {
            String subName = this.name + "_" + nameIndex;
            DecisionVariable subVariable = new DecisionVariable(subName, DecisionVariableType.BINARY);
            this.variablesToCoefficent.put(subVariable, allowedValue);
            this.subVariables.add(subVariable);
            this.chooseOnlyOneConstraint.addTermLhs(1, subVariable);
            this.linearExpressionToInsert.addTerm(allowedValue, subVariable);
            nameIndex++;
        }
    }

    public Double findCoefficient(DecisionVariable decisionVariable) {
        return this.variablesToCoefficent.get(decisionVariable);
    }

    public LinearExpression getLinearExpressionToInsert() {
        return linearExpressionToInsert;
    }

    public List<DecisionVariable> getSubVariables() {
        return subVariables;
    }

    public LinearConstraint getChooseOnlyOneConstraint() {
        return chooseOnlyOneConstraint;
    }
}