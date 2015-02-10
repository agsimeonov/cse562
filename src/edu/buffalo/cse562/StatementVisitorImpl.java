package edu.buffalo.cse562;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class StatementVisitorImpl implements StatementVisitor {

  @Override
  public void visit(Select select) {
    // TODO Auto-generated method stub
    // System.out.println("Select: " + select.getSelectBody());
    select.getSelectBody().accept(new SelectVisitorImpl());
    // TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
    // List<String> tableList = tablesNamesFinder.getTableList(selectStatement);

  }

  @Override
  public void visit(Delete delete) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Update update) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Insert insert) {
    insert.getItemsList().accept(new ItemsListVisitorImpl());
  }

  @Override
  public void visit(Replace replace) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Drop drop) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Truncate truncate) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CreateTable createTable) {
    System.out.println("Table:" + createTable.getTable().getName());
  }

}
