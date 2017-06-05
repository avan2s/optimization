package org.andy.optimization.model;

import org.andy.optimization.enums.DecisionVariableType;

/**
 * Created by Andy on 05.06.2017.
 */
public class DecisionVariable {

    private DecisionVariableType variableType;
    private String name;
    private int index = -1;
    private double lowerInclusiveBound;
    private double upperInclusiveBound;


    public DecisionVariable(String name, DecisionVariableType variableType) {
        this.name = name;
        this.variableType = variableType;
        this.initBounds();
    }

    public DecisionVariable(String name, DecisionVariableType variableType, double lowerInclusiveBound) {
        this.variableType = variableType;
        this.name = name;
        this.initBounds();
        this.lowerInclusiveBound = lowerInclusiveBound;
    }

    public DecisionVariable(String name, DecisionVariableType variableType, double lowerInclusiveBound, double upperInclusiveBound) {
        this.variableType = variableType;
        this.name = name;
        this.initBounds();
        this.lowerInclusiveBound = lowerInclusiveBound;
        this.upperInclusiveBound = upperInclusiveBound;
    }

    private void initBounds(){
        if(variableType == DecisionVariableType.BINARY){
            this.lowerInclusiveBound = 0;
            this.upperInclusiveBound = 1;
        }else {
            this.lowerInclusiveBound = -Double.MAX_VALUE;
            this.upperInclusiveBound = Double.MAX_VALUE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DecisionVariable that = (DecisionVariable) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return this.name;
    }


    public int getIndex() {
        return index;
    }


    public DecisionVariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(DecisionVariableType variableType) {
        this.variableType = variableType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getLowerInclusiveBound() {
        return lowerInclusiveBound;
    }

    public void setLowerInclusiveBound(double lowerInclusiveBound) {
        this.lowerInclusiveBound = lowerInclusiveBound;
    }

    public double getUpperInclusiveBound() {
        return upperInclusiveBound;
    }

    public void setUpperInclusiveBound(double upperInclusiveBound) {
        this.upperInclusiveBound = upperInclusiveBound;
    }
}
