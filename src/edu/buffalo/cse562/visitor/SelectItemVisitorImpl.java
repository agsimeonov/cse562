package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class SelectItemVisitorImpl implements SelectItemVisitor {

  @Override
  public void visit(AllColumns allColumns) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(AllTableColumns allTableColumns) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(SelectExpressionItem selectExpressionItem) {
    selectExpressionItem.getExpression().accept(new ExpressionVisitorImpl());
  }
}
