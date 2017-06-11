package org.andy.optimization.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 05.06.2017.
 */
public class LinearExpression {
    private List<LinearTerm> terms;
    private double constant = 0;

    public LinearExpression(){
        this.terms = new ArrayList<>();
    }

    public void addTerm(double coefficient, DecisionVariable decisionVariable) {
        this.terms.add(new LinearTerm(coefficient, decisionVariable));
    }

    public void addConstant(double value) {
        this.constant = this.constant + value;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public List<LinearTerm> getTerms() {
        return terms;
    }

    public double getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(LinearTerm term : terms){
            if(sb.length() > 0){
                sb.append(" + ");
            }
            sb.append(term.getCoefficent()).append("*").append(term.getDecisionVariable().getName());
        }
        int termSize = terms.size();
        if(termSize > 0 && this.getConstant() > 0){
            sb.append(" + ").append(this.getConstant());
        }else{
            sb.append(this.getConstant());
        }

        return sb.toString();
    }
}
