package edu.buffalo.cse562;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

public class ItemsListVisitorImpl implements ItemsListVisitor {

  @Override
  public void visit(SubSelect subSelect) {
    subSelect.getSelectBody().accept(new SelectVisitorImpl());

  }

  @Override
  public void visit(ExpressionList expressionList) {
    System.out.println(expressionList.getExpressions());

  }
}
