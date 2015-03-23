package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

public class SplitSelect {
  public static List<Expression> splitConjunctive(Expression expression) {
    List<Expression> list = new ArrayList<Expression>();
    
    if (expression instanceof AndExpression) {
      AndExpression and = (AndExpression) expression;
      list.addAll(splitConjunctive(and.getLeftExpression()));
      list.addAll(splitConjunctive(and.getRightExpression()));
    } else {
      list.add(expression);
    }
    
    return list;
  }
}
