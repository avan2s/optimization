package org.andy.optimization.api;

import org.andy.optimization.model.DecisionVariable;
import org.andy.optimization.model.LinearConstraint;
import org.andy.optimization.model.LinearObjective;

/**
 * Created by Andy on 05.06.2017.
 */
public interface ILinearOptimizationModel {
    void addDecisionVariable(DecisionVariable variable);

    void addLinearConstraint(LinearConstraint constraint);

    void setObjective(LinearObjective objective);
}
