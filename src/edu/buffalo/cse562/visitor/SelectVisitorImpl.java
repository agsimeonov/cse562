package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.Union;

public class SelectVisitorImpl implements SelectVisitor {

  @Override
  public void visit(PlainSelect plainSelect) {
    System.out.println(plainSelect);
    System.out.println(plainSelect.getFromItem());
  }

  @Override
  public void visit(Union union) {
    // TODO Auto-generated method stub
  }
}
