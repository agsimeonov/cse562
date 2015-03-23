package edu.buffalo.cse562.optimizer;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

public class ExpressionRewriter {
  private final Expression expression;
  
  public ExpressionRewriter(Expression expression) {
    this.expression = expression;
  }
  
//  public List<Expression> splitConjuntive(Expression expression) {
//    List<Expression> out = new ArrayList<Expression>();
//
//    if (expression instanceof AndExpression){
//      AndExpression andExpression = (AndExpression) expression;
//      out.addAll(splitConjunctive())
//      ret.addAll(
//        splitAndClauses(a.getLeftExpression())
//      );
//      ret.addAll(
//        splitAndClauses(a.getRightExpression())
//      );
//    } else {
//      expression.ad
//    }
//    return null;
//  }

  private class ConjunctiveTree {
    private Expression expression;
    private ConjunctiveTree base;
    private ConjunctiveTree left;
    private ConjunctiveTree right;
    
    private ConjunctiveTree(ConjunctiveTree base, Expression expression) {
      this.base = base;
      
      if (expression instanceof AndExpression) {
        AndExpression andExpression = (AndExpression) expression;
        left = new ConjunctiveTree(this, andExpression.getLeftExpression());
        right = new ConjunctiveTree(this, andExpression.getRightExpression());
      } else {
        this.expression = expression;
      }
    }
    
    private Expression reconstruct() {
      Expression out;
      
      if (this.expression != null) {
        return out = expression;
      } else if (left != null && right != null) {
        out = new AndExpression(left.expression, right.expression);
      } else if (left != null) {
        out = left.expression;
      } else if (right != null) {
        out = right.expression;
      } else {
        out = new AndExpression(left.reconstruct(), right.reconstruct());
      }
      
      return out;
    }
    
    private boolean hasExpression(AndExpression andExpression) {
      return false;
    }
  }
}
