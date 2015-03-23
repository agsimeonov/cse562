package edu.buffalo.cse562.optimizer;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

public class ExpressionRewriter {
  private final Expression expression;
  
  public ExpressionRewriter(Expression expression) {
    this.expression = expression;
  }

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
      
      if (expression != null) {
        out = expression;
      } else if (left == null && right == null) {
        out = null;
      } else if (left != null && right != null) {
        out = new AndExpression(left.reconstruct(), right.reconstruct());
      } else if (left != null) {
        return left.reconstruct();
      } else {
        return right.reconstruct();
      }
      
      return out;
    }
    
//    private boolean removeExpression(EqualsTo andExpression) {
//      if (expression != null) {
//        if (expression in)
//      }
//      return false;
//    }
  }
}
