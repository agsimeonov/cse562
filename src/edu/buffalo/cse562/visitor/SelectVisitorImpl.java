package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.database.Database;
import edu.buffalo.cse562.database.DbTable;

public class SelectVisitorImpl implements SelectVisitor, FromItemVisitor {
  private DbTable fromResult;

  @Override
  public void visit(PlainSelect plainSelect) {
    System.out.println(plainSelect);
    System.out.println(plainSelect.getFromItem());
    plainSelect.getFromItem().accept(this);
    System.out.println(fromResult);
  }

  @Override
  public void visit(Union union) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(Table table) {
    // TODO Auto-generated method stub
    fromResult = Database.getTable(table.getName());
  }

  @Override
  public void visit(SubSelect subSelect) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(SubJoin subjoin) {
    // TODO Auto-generated method stub
    
  }
}
