package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.TableManager;

public class SelectManager implements SelectVisitor, FromItemVisitor {
  private DataTable fromResult;

  @Override
  public void visit(PlainSelect plainSelect) {
    System.out.println(plainSelect);
    System.out.println(plainSelect.getSelectItems().get(0));
    System.out.println(plainSelect.getFromItem());
    plainSelect.getFromItem().accept(this);
//    System.out.println(fromResult);
  }

  @Override
  public void visit(Union union) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(Table table) {
    // TODO Auto-generated method stub
    fromResult = TableManager.getTable(table.getName());
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
