package edu.buffalo.cse562.parsetree;

import net.sf.jsqlparser.expression.Expression;

public class ParseTree {
  private Expression expression;
  private ParseTree base;
  private ParseTree left;
  private ParseTree right;
  
  public ParseTree(Expression value, ParseTree baseNode, ParseTree leftNode, ParseTree rightNode) {
    expression = value;
    base = baseNode;
    left = leftNode;
    right = rightNode;
  }

  public Expression getExpression() {
    return expression;
  }
}
